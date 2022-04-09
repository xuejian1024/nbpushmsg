package com.guanzhijie.nbpushmsg;

import com.alibaba.fastjson.JSON;
import com.ctiot.aep.mqmsgpush.sdk.IMsgConsumer;
import com.ctiot.aep.mqmsgpush.sdk.IMsgListener;
import com.ctiot.aep.mqmsgpush.sdk.MqMsgConsumer;
import com.guanzhijie.nbpushmsg.entity.NBCamera;
import com.guanzhijie.nbpushmsg.response.MQResponse;
import com.guanzhijie.nbpushmsg.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.shade.com.google.gson.Gson;
import org.apache.pulsar.shade.com.google.gson.JsonObject;
import org.apache.pulsar.shade.org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.annotation.PostConstruct;
import javax.jms.Queue;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableJms //启动消息队列
public class NbpushmsgApplication {


    //注入存放消息的队列，用于下列方法一
    @Autowired
    private Queue queue;

    //注入springboot封装的工具类
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public static void main(String[] args) {
        SpringApplication.run(NbpushmsgApplication.class, args);
    }


    @PostConstruct
    public void init() {

        String server = "msgpush.ctwing.cn:16651"; //消息服务地址
        String tenantId = "2000080391";//租户ID
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDAwMDgwMzkxIn0.mXqY4QYzNSKtHxtRMtLZZLNczcUakr_SkwGAtO4kqzo";//身份认证token串
        String certFilePath = ""; //直接填空字符串，CA证书，JDK已经内置相关根证书，无需指定

        System.out.println("hot-fix1");
        Map<String, NBCamera> map = new HashMap<String,NBCamera>();
        NBCamera camera = new NBCamera();
        camera.setNb_device_id("861541064342026");
        camera.setNb_device_name("1-101");
        camera.setCamera_ip("192.168.1.10");
        camera.setCamera_port("9090");
        camera.setCamera_id("10038$0");
        map.put("861541064342026",camera);

        camera = new NBCamera();
        camera.setNb_device_id("460113118743732");
        camera.setNb_device_name("1-101");
        camera.setCamera_ip("192.168.1.10");
        camera.setCamera_port("9090");
        camera.setCamera_id("10038$0");
        map.put("460113118743732",camera);


        camera = new NBCamera();
        camera.setNb_device_id("460113118743733");
        camera.setNb_device_name("1-102");
        camera.setCamera_ip("192.168.1.12");
        camera.setCamera_port("9090");
        camera.setCamera_id("10038$1");
        map.put("460113118743733",camera);


        //创建消息接收Listener
        IMsgListener msgListener = new IMsgListener() {
            @Override
            public void onMessage(String msg) {
                //接收消息
                System.out.println("接收消息::::" + msg);
                //消息处理
                JsonObject message = new Gson().fromJson(msg, JsonObject.class);
                if (message != null && message.get("payload") != null) {
                    JsonObject payload = message.get("payload").getAsJsonObject();
                    long timestamp = message.get("timestamp").getAsLong(); //报警时间
                    String date = DateUtil.timeStamp2Date(String.valueOf(timestamp / 1000), "yyyy-MM-dd HH:mm:ss");
                    String imei = message.get("IMEI").getAsString();//设备编号
                    if (StringUtils.isBlank(imei)) {
                        return;
                    }
                    if (payload != null && payload.has("APPdata")) {
                        String data = payload.get("APPdata").getAsString();
                        byte[] x = Base64.getDecoder().decode(data);
                        data = Hex.encodeHexString(x);
                        if (StringUtils.isBlank(data)) {
                            return;
                        }

                        String up = data.trim().substring(6, 8);// 02:事件上报
                        if (StringUtils.isBlank(up) || !up.equals("02")) {
                            return;
                        }

                        String no = DateUtil.hex2Str(data.trim().substring(12, 42)); //IMEI:16进制转ascii
                        if (StringUtils.isBlank(no)) {
                            return;
                        }

                        if (!imei.equals(no)) {
                            return;
                        }

                        String type = data.trim().substring(42, 44);
                        if (StringUtils.isBlank(type) || (!type.equals("02") && !type.equals("03"))) {
                            return;
                        }

                        String status = data.trim().substring(44, 46);
                        if (StringUtils.isBlank(status) || (!status.equals("00") && !status.equals("01"))) {
                            return;
                        }
                        MQResponse response = new MQResponse();
                        response.setDatetime(date);
                        response.setImei(no);
                        response.setStatus(status);
                        response.setType(type);
                        // response.setNbCamera(map.get(imei));

                        String param = JSON.toJSONString(response);
                        System.out.println("=====解析数据值start======");
                        System.out.println("获取数据payload::::" + payload);
                        System.out.println("上报类型::::" + up + "::::设备型号IMEI::::" + no + "::::事件类型::::" + type + "::::门窗状态::::" + status);
                        System.out.println("=====解析数据值end======");
                        System.out.println("send json data::::" + param);

                        jmsMessagingTemplate.convertAndSend(queue, param);

                    }
                }
            }
        };


        //创建消息接收类
        IMsgConsumer consumer = new MqMsgConsumer();
        try {
            //初始化
            /**
             * @param server  消息服务server地址
             * @param tenantId 租户Id
             * @param token    用户认证token
             * @param certFilePath 证书文件路径
             * @param topicNames   主题名列表，如果该列表为空或null，则自动消费该租户所有主题消息
             * @param msgListener 消息接收者
             * @return 是否初始化成功
             */
            List list = new ArrayList<>();
            list.add("MC");
            consumer.init(server, tenantId, token, certFilePath, list, msgListener);

            //开始接收消息
            consumer.start();


            //程序退出时，停止接收、销毁
            //consumer.stop();
            //consumer.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("exit");
    }

    //@PostConstruct
    public void test() {

        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<MQResponse> list = new ArrayList<>();
                MQResponse response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342030");
                response.setStatus("01");
                response.setType("02");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342029");
                response.setStatus("01");
                response.setType("02");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342028");
                response.setStatus("01");
                response.setType("02");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342027");
                response.setStatus("01");
                response.setType("02");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342026");
                response.setStatus("01");
                response.setType("02");
                list.add(response);

                for (MQResponse mqResponse : list) {
                    String param = JSON.toJSONString(mqResponse);
                    System.out.println("send json data::::" + param);

                    jmsMessagingTemplate.convertAndSend(queue, param);

                }
            }
        }, 1, 60, TimeUnit.SECONDS);



        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<MQResponse> list = new ArrayList<>();
                MQResponse response = new MQResponse();

                response.setDatetime(getDate());
                response.setImei("861541064342028");
                response.setStatus("00");
                response.setType("03");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342029");
                response.setStatus("00");
                response.setType("03");
                list.add(response);


                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342026");
                response.setStatus("00");
                response.setType("03");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342027");
                response.setStatus("00");
                response.setType("03");
                list.add(response);

                response = new MQResponse();
                response.setDatetime(getDate());
                response.setImei("861541064342030");
                response.setStatus("00");
                response.setType("03");
                list.add(response);


                for (MQResponse mqResponse : list) {
                    String param = JSON.toJSONString(mqResponse);
                    System.out.println("send json data::::" + param);

                    jmsMessagingTemplate.convertAndSend(queue, param);
                }
            }
        }, 30, 90, TimeUnit.SECONDS);

    }

    private String getDate() {
        Date d = new Date();

        System.out.println(d);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateNowStr = sdf.format(d);

        return dateNowStr;
    }


}
