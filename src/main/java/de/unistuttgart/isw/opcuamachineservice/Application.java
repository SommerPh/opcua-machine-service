package de.unistuttgart.isw.opcuamachineservice;

import com.siemens.ct.exi.exceptions.EXIException;
import de.unistuttgart.isw.opcuamachineservice.opcua.Subscriptions;
import de.unistuttgart.isw.opcuamachineservice.utils.Json2NodeId;
import de.unistuttgart.isw.opcuamachineservice.utils.KafkaHelper;
import de.unistuttgart.isw.opcuamachineservice.utils.XMLHelper;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageBus;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class Application {
    public static void main(String[] args) throws Exception {
        final Logger logger = LogManager.getLogger("Application");
        if(args.length > 0){
            File file = new File(args[0]);
            System.setProperty("http.agent", "Mozilla/5.0");
            Subscriptions subscriptions = new Subscriptions();
            NodeId[] ids = Json2NodeId.json2NodeId(file);
            List<UaMonitoredItem> itemList = subscriptions.createMonitoredItemsFromNodes(ids).get();
            MessageBus bus = new MessageBus();
            Map<String,MessageSender> messageSenders = KafkaHelper.creadeSendersFromIds(ids, bus);
            for (UaMonitoredItem item : itemList) {
                item.setValueConsumer((uaMonitoredItemitem, value) -> {
                    String id = uaMonitoredItemitem.getReadValueId().getNodeId().getIdentifier().toString();
                    try {
                        messageSenders.get(id).send(XMLHelper.createXML(value));
                        logger.info("Message send for Node: " + id);
                    } catch (IOException | SAXException | EXIException e) {
                        logger.error("Could nod send Message for Node: " + id);
                        e.printStackTrace();
                    }
                });
                logger.info("Created Listener for MonitoredItem: " + item.getReadValueId().getNodeId().getIdentifier().toString());
            }
            while (true);
        }else{
            logger.error("Please provide a valid json-Filepath as command line argument");
        }
    }
}
