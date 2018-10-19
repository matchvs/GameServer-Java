import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.Simple;

public class App extends GameServerRoomEventHandler {

    Logger log = LoggerFactory.getLogger("App");


    public static void main(String[] args) {
        String[] path = new String[1];
        /**
         * 本地调试时在此处填写自己config.Json的绝对路径,正式发布上线注释下面代码即可。
         */
//        path[0] = "C:\\Users\\xing\\Desktop\\java-gameServe\\gsmeta";
        try {
            Main.main(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onGameClientEvent(Simple.Package.Frame clientEvent, StreamObserver<Simple.Package.Frame> clientChannel) throws InvalidProtocolBufferException {
        super.onGameClientEvent(clientEvent, clientChannel);
        switch (clientEvent.getCmdId()) {
        }
        return false;
    }



    @Override
    public void onStart() {
        log.info("onStart");

    }

    @Override
    public void onStop() {
        log.info("onStop");
    }

    @Override
    public boolean onRoomEvent(Room room, Simple.Package.Frame receivedFrame, StreamObserver<Simple.Package.Frame> clientChannel) {
        switch (receivedFrame.getCmdId()) {
            // 收到客户端发送的消息。
            case Gshotel.HotelGsCmdID.HotelBroadcastCMDID_VALUE:
                Gshotel.HotelBroadcast boardMsg = null;
                try {
                    boardMsg = Gshotel.HotelBroadcast.parseFrom(receivedFrame.getMessage());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                String msg = boardMsg.getCpProto().toStringUtf8();
                log.info(msg);
                break;
        }
        return false;
    }


}
