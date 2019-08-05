package cn.okcoming.bigdata;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ESUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    //
    private static String srcClustName = "es-cn-4590otxtx00065hdd";
    private static String srcIndexName = "itslaw";
    private static String srcTypeName = "itslaw";
    private static String srcIp = "es-cn-4590otxtx00065hdd.elasticsearch.aliyuncs.com";
    private static int srcPort = 9200;

    private static String tagClustName = "es-cn-v6416z67g000kvhc3";
    private static String tagIndexName = "itslaw";
    private static String tagTypeName = "itslaw";
    private static String tagIp = "es-cn-v6416z67g000kvhc3.elasticsearch.aliyuncs.com";
    private static int tagPort = 9200;

    private static String user = "elastic";
    private static String password = "xxxxx";

    public static void main(String[] args) throws Exception {

        int fromIndex = 0;
        if(args.length > 0){
            fromIndex = Integer.parseInt(args[0]);
        }


        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(user, password));

        RestClient client = RestClient
                .builder(new HttpHost(srcIp, srcPort, "http"))
                .setDefaultHeaders(new Header[]{new BasicHeader("Content-Type","application/json")})
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
        RestClient client2 = RestClient
                .builder(new HttpHost(tagIp, tagPort, "http"))
                .setDefaultHeaders(new Header[]{new BasicHeader("Content-Type","application/json")})
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        //esToEs(fromIndex,  client, client2);
        esToEs3(client,client2);
    }


    private static String stream2string(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        return new String(bytes,StandardCharsets.UTF_8);
    }
    /**
     * 数据拷贝  size+from 不同分页查询的数据会有重复
     * 这种分页方式只适合少量数据，因为随from增大，查询的时间就会越大，而且数据量越大，查询的效率指数下降
     *
     */
    public static void esToEs(int fromIndex,  RestClient client, RestClient client2) throws IOException {
        int fetchSize = 1000;
        while (true){
            Map<String,Object> params = new HashMap<>();
            params.put("size",fetchSize);
            params.put("from",fromIndex);
            HttpEntity entity = new NStringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON);
            Response response = client.performRequest("POST","/" + srcIndexName + "/_search?pretty", Collections.emptyMap(),entity,new Header[]{});
            Integer total = 0;
            if(response.getStatusLine().getStatusCode() == 200){
                System.out.println("fromIndex="+fromIndex);
                Map data = new ObjectMapper().readValue(stream2string(response.getEntity().getContent()),Map.class);
                Map hits1 = (Map) data.get("hits");
                total = (Integer) hits1.get("total");
                if(fromIndex > total) break;
                List<Map> list = (List<Map>) hits1.get("hits");
                save2Target(client2,list);
            }else{
                System.out.println("error break fromIndex="+fromIndex);
                System.out.println(response);
                break;
            }
            fromIndex = fromIndex + fetchSize;
            if(fromIndex > total) break;
        }

        System.out.println("end");
        client.close();
        client2.close();
    }

    private static AtomicInteger counter = new AtomicInteger(0);
    private static void save2Target(RestClient client2,List<Map> list) throws IOException {
        System.out.println("找到记录="+list.size());
        for (Map hits2 : list){
            Map source= (Map) hits2.get("_source");
            String id = (String) source.get("itslawId");
            String content = objectMapper.writeValueAsString(source);

            try{
                Response response2 = client2.performRequest("PUT","/" + tagIndexName + "/" + tagTypeName + "/" + id,
                        Collections.emptyMap(),new NStringEntity(content, ContentType.APPLICATION_JSON),new Header[]{});
                System.out.println(response2.getStatusLine() + " " + id + " " + counter.incrementAndGet());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 数据拷贝 还有问题
     * 为了解决上面的问题，elasticsearch提出了一个scroll滚动的方式，这个滚动的方式原理就是通过每次查询后，返回一个scroll_id。
     * 根据这个scroll_id 进行下一页的查询。可以把这个scroll_id理解为通常关系型数据库中的游标
     *
     * 可以把 scroll 分为初始化和遍历两步
     * POST /itslaw/_search?scroll=1m
     * POST /_search/scroll?scroll=1m
     */
    public static void esToEs2(RestClient client, RestClient client2) throws IOException {
        int fetchSize = 1000;

        Map<String,Object> params = new HashMap<>();
        params.put("size",fetchSize);
        HttpEntity entity = new NStringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON);
        Response response = client.performRequest("POST","/" + srcIndexName + "/_search?scroll=1m", Collections.emptyMap(),entity,new Header[]{});
        if(response.getStatusLine().getStatusCode() == 200){
            Map data = new ObjectMapper().readValue(stream2string(response.getEntity().getContent()),Map.class);
            String  _scroll_id = (String) data.get("_scroll_id");
            Map<String,Object> params2 = new HashMap<>();
            params2.put("scroll_id",_scroll_id);
            HttpEntity entity2 = new NStringEntity(objectMapper.writeValueAsString(params2), ContentType.APPLICATION_JSON);
            while (true) {
                Response response2 = client.performRequest("POST","/_search/scroll?scroll=1m", Collections.emptyMap(),entity2,new Header[]{});
                if(response2.getStatusLine().getStatusCode() == 200){
                    Map hits1 = (Map) data.get("hits");
                    List<Map> list = (List<Map>) hits1.get("hits");
                    if(list.size() == 0){
                        //说明遍历完了 结束
                        break;
                    }
                    save2Target(client2,list);
                }else {
                    System.out.println(response);
                    break;
                }
            }
        }

        System.out.println("end");
        client.close();
        client2.close();
    }


    public static void esToEs3(RestClient client, RestClient client2) throws IOException {
        int fetchSize = 1000;

        Map<String,Object> params = new HashMap<>();
        params.put("size",fetchSize);
        Map<String,Object> sortParams = new HashMap<>();
        sortParams.put("_uid","asc");
        params.put("sort",new Object[]{sortParams});
        HttpEntity entity = new NStringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON);
        Response response = client.performRequest("POST","/" + srcIndexName  + "/" + srcTypeName + "/_search", Collections.emptyMap(),entity,new Header[]{});
        if(response.getStatusLine().getStatusCode() == 200){
            Map data = new ObjectMapper().readValue(stream2string(response.getEntity().getContent()),Map.class);
            Map hits1 = (Map) data.get("hits");
            List<Map> list = (List<Map>) hits1.get("hits");
            while(list.size()>0){
                System.out.println("找到记录="+list.size());
                save2Target(client2,list);
                Map last = list.get(list.size()-1);
                List<String> sort = (List<String>) last.get("sort");
                params = new HashMap<>();
                params.put("size",fetchSize);
                params.put("search_after",sort);
                sortParams = new HashMap<>();
                sortParams.put("_uid","asc");
                params.put("sort",new Object[]{sortParams});
                entity = new NStringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON);
                response = client.performRequest("POST","/" + srcIndexName  + "/" + srcTypeName + "/_search", Collections.emptyMap(),entity,new Header[]{});
                if(response.getStatusLine().getStatusCode() == 200){
                    data = new ObjectMapper().readValue(stream2string(response.getEntity().getContent()),Map.class);
                    hits1 = (Map) data.get("hits");
                    list = (List<Map>) hits1.get("hits");
                }else{
                    System.out.println(response);
                    break;
                }
            }
        }

        System.out.println("all count : "+counter.get());
        client.close();
        client2.close();
    }
}
