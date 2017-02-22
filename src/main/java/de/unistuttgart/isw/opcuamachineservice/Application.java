package de.unistuttgart.isw.opcuamachineservice;

import de.unistuttgart.isw.opcuamachineservice.opcua.Subscriptions;
import de.unistuttgart.isw.opcuamachineservice.utils.Json2NodeId;
import de.unistuttgart.isw.opcuamachineservice.utils.XMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.File;
import java.util.List;

/**
 * @author Philipp Sommer
 * @version 1.0
 * @date 16.02.17
 * @copyright ISW Uni Stuttgart
 * Based on opcua machine service:
 * author Timur Tasci
 * version 1.0
 * date 11.08.16
 * copyright ISW Uni Stuttgart
 */
public class Application {
    public static void main(String[] args) throws Exception {
        final Logger logger = LogManager.getLogger("Application");
        final String RABBIT_MQ_SERVER_HOST = "rabbitmq.server.host";
        final String RABBIT_MQ_SERVER_PORT = "rabbitmq.server.port";
        final String EXCHANGE_NAME = "machinedata";

        if(args.length > 0){
            File file = new File(args[0]);
            System.setProperty("http.agent", "Mozilla/5.0");
            Subscriptions subscriptions = new Subscriptions();
            NodeId[] ids = Json2NodeId.json2NodeId(file);
            List<UaMonitoredItem> itemList = subscriptions.createMonitoredItemsFromNodes(ids).get();

            //initialize rabbit mq
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(System.getenv(RABBIT_MQ_SERVER_HOST));
            factory.setPort(Integer.parseInt(System.getenv(RABBIT_MQ_SERVER_PORT)));
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            //MessageBus bus = new MessageBus();
            //Map<String,MessageSender> messageSenders = KafkaHelper.creadeSendersFromIds(ids, bus);
            for (UaMonitoredItem item : itemList) {
                item.setValueConsumer((uaMonitoredItemitem, value) -> {
                    String id = uaMonitoredItemitem.getReadValueId().getNodeId().getIdentifier().toString();
                    try {
                        //messageSenders.get(id).send(XMLHelper.createXML(value));
                        //System.out.println("Message send for Node: " + id);
                        //double[] data = (double[]) value.getValue().getValue();
                        System.out.println(value.toString());
                        String message = XMLHelper.createXML(value);
                        channel.basicPublish(EXCHANGE_NAME, id, null, message.getBytes());
                        logger.info("Message send for Node: " + id+ "\nMessage: "+message);
                    } catch (IOException e) {
                        logger.error("Could nod send Message for Node: " + id);
                        e.printStackTrace();
                    }
                });
                //System.out.println("Created Listener for MonitoredItem: " + item.getReadValueId().getNodeId().getIdentifier().toString());
                logger.info("Created Listener for MonitoredItem: " + item.getReadValueId().getNodeId().getIdentifier().toString());
            }
            while (true);
        }else{
            //System.out.println("Please provide a valid json-Filepath as command line argument");
            logger.error("Please provide a valid json-Filepath as command line argument");
        }
    }
}
