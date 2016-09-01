package de.unistuttgart.isw.opcuamachineservice.opcua;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import static java.util.Arrays.stream;

/**
 *
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class ClientManager {
    private static String OPC_UA_SERVER_ADRESS = "opcua.server";
    private static final Logger logger = LogManager.getLogger("ClientManager");

    private static ClientManager manager;
    private static OpcUaClient client;

    private ClientManager() {
        EndpointDescription[] endpoints;
        logger.info("Connecting to Server " + System.getenv(OPC_UA_SERVER_ADRESS));
        try {
            endpoints = UaTcpStackClient
                    .getEndpoints(System.getenv(OPC_UA_SERVER_ADRESS)).get();
            EndpointDescription endpoint = stream(endpoints)
                    .findFirst()
                    .orElseThrow(() -> new Exception("no desired endpoints returned"));
            OpcUaClientConfig clientConfig = OpcUaClientConfig.builder()
                    .setEndpoint(endpoint)
                    .build();
            client = new OpcUaClient(clientConfig);
            client.connect();
            logger.info("Connection established! OPC UA Adress : " + System.getenv(OPC_UA_SERVER_ADRESS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ClientManager getInstance() {
        if (manager == null) {
            manager = new ClientManager();
        }
        return manager;
    }

    public OpcUaClient getClient() {
        return client;
    }
}
