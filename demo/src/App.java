import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.Gsdirectory;
import stream.Simple;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class App extends GameServerRoomEventHandler {


    public static Logger log = LoggerFactory.getLogger("App");
    private static AtomicLong clock = new AtomicLong();

    public static void main(String[] args) {
        String path = null;
        /**
         * 本地调试时在此处填写自己config.Json的绝对路径,正式发布上线注释下面代码即可。
         */
//        path = "D:\\git\\GameServer-Java\\src\\Config.json";
        try {
            Main.main(new String[]{path});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void benchMark() {
        int i1 = 10000 * 10000;
        long lastTime = System.currentTimeMillis();
        for (int i = 0; i < i1; i++) {
            clock.getAndIncrement();
        }
        log.info("BenchMark: {}/624", (System.currentTimeMillis() - lastTime));


    }

    @Override
    public boolean onReceive(Simple.Package.Frame clientEvent, StreamObserver<Simple.Package.Frame> clientChannel) {
        try {
            Gsmvs.Request request = Gsmvs.Request.parseFrom(clientEvent.getMessage());
            log.info(" cmd:" + clientEvent.getCmdId());
            switch (clientEvent.getCmdId()) {
                case Gshotel.HotelGsCmdID.HotelBroadcastCMDID_VALUE:
                    Gshotel.HotelBroadcast boardMsg = Gshotel.HotelBroadcast.parseFrom(clientEvent.getMessage());
                    String msg = boardMsg.getCpProto().toStringUtf8();
                    log.info("收到消息：" + msg);
                    examplePush(boardMsg.getRoomID(), msg);
                    break;
                case Gsmvs.MvsGsCmdID.MvsCreateRoomReq_VALUE:
                    log.info("创建房间成功: 房间ID：" + request.getRoomID());
                    break;
                //删除房间
                case Gshotel.HotelGsCmdID.HotelCloseConnet_VALUE:
                    log.info("删除房间: 房间ID：" + request.getRoomID());
                    break;
                // 玩家checkIn
                case Gshotel.HotelGsCmdID.HotelPlayerCheckin_VALUE:
                    log.info("玩家checkIn:  userID:" + request.getUserID());
                    break;
                case Gsmvs.MvsGsCmdID.MvsJoinRoomReq_VALUE:
                    log.info("进入房间成功  玩家" + request.getUserID() + "进入房间，房间ID为：" + request.getRoomID());
                    break;
                case Gsmvs.MvsGsCmdID.MvsKickPlayerReq_VALUE:
                    log.info("踢人成功: 房间：" + request.getRoomID() + "玩家：" + request.getUserID() + "被踢出");
                    break;
                case Gsmvs.MvsGsCmdID.MvsLeaveRoomReq_VALUE:
                    log.info("离开房间成功： 玩家" + request.getUserID() + "离开房间，房间ID为：" + request.getRoomID());
                    break;
                case Gsmvs.MvsGsCmdID.MvsJoinOpenReq_VALUE:
                    log.info("房间打开成功:  roomID：" + request.getRoomID());
                    break;
                case Gsmvs.MvsGsCmdID.MvsJoinOverReq_VALUE:
                    log.info("房间关闭成功: roomID：" + request.getRoomID());
                    break;
                case Gsmvs.MvsGsCmdID.MvsSetRoomPropertyReq_VALUE:
                    Gsmvs.SetRoomPropertyReq roomPropertyReq = Gsmvs.SetRoomPropertyReq.parseFrom(clientEvent.getMessage());
                    log.info("修改房间属性: ");
                    log.info(roomPropertyReq + "");
                    break;
                case Gsmvs.MvsGsCmdID.MvsGetRoomDetailPush_VALUE:
                    Gsmvs.RoomDetail roomDetail = Gsmvs.RoomDetail.parseFrom(clientEvent.getMessage());
                    log.info("主动获取房间回调:");
                    log.info(roomDetail + "");
                    break;
                case Gshotel.HotelGsCmdID.GSSetFrameSyncRateNotifyCMDID_VALUE:
                    Gshotel.GSSetFrameSyncRateNotify setFrameSyncRateNotify = Gshotel.GSSetFrameSyncRateNotify.parseFrom(clientEvent.getMessage());
                    log.info("帧率通知");
                    log.info(setFrameSyncRateNotify + "");
                    break;
                case Gshotel.HotelGsCmdID.GSFrameDataNotifyCMDID_VALUE:
                    Gshotel.GSFrameDataNotify frameDataNotify = Gshotel.GSFrameDataNotify.parseFrom(clientEvent.getMessage());
                    log.info("帧数据通知");
                    log.info(frameDataNotify + "");
                    break;
                case Gshotel.HotelGsCmdID.GSFrameSyncNotifyCMDID_VALUE:
                    Gshotel.GSFrameSyncNotify frameSyncNotify = Gshotel.GSFrameSyncNotify.parseFrom(clientEvent.getMessage());
                    log.info("帧同步通知");
                    log.info(frameSyncNotify + "");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * API使用示例
     *
     * @param msg
     */
    public void examplePush(long roomID, String msg) {
        String[] strArray = msg.split(":");
        switch (strArray[0]) {
            case "kickPlayer":
                kickPlayer(roomID, new Integer(strArray[1]));
                break;
            case "createRoom":
                createRoom(new Integer(strArray[1]));
                break;
            case "joinOver":
                joinOver(roomID);
                break;
            case "joinOpen":
                joinOpen(roomID);
                break;
            case "getRoomDetail":
                getRoomDetail(roomID);
                break;
            case "setRoomProperty":
                setRoomProperty(roomID, strArray[1]);
                break;
            case "setFrameSyncRate":
                setFrameSyncRate(roomID, new Integer(strArray[1]), new Integer(strArray[2]), new Integer(strArray[3]));
                break;
            case "frameBroadcast":
                frameBroadcast(roomID, strArray[1], new Integer(strArray[2]));
                break;
            case "touchRoom":
                touchRoom(roomID, new Integer(strArray[1]));
                break;
            case "destroyRoom":
                destroyRoom(Long.parseLong(strArray[1]));
                break;
            case "getCacheData":
                getCacheData(roomID, new Integer(strArray[1]));
                break;
        }
    }

    public AtomicLong index = new AtomicLong(0);
    private long lastTime = System.currentTimeMillis();

    @Override
    public void onStart() {
        log.info("onStart");
//        Main.gameServer.setInterval(new Runnable() {
//            @Override
//            public void run() {
//                long a = index.incrementAndGet();
//                if (a > 1000) {
//                    index.set(0);
//                    lastTime = System.currentTimeMillis();
//                }
//                log.info("time: invoke"  );
//            }
//        }, 10);
//        App app = new App();
//        app.createRoom(10);

    }

    @Override
    public void onStop() {
        log.info("onStop");
    }

}
