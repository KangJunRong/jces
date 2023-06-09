/*
package com.ecp.jces.file.config;


import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class Config {
	public static StorageClient storageClient;
	private static final String CONF_NAME = "fdfs_client.conf";
	@PostConstruct
	public void init() throws IOException, MyException {
		ClientGlobal.init(CONF_NAME);
		System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
		System.out.println("charset=" + ClientGlobal.g_charset);
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getTrackerServer();
		storageClient = new StorageClient(trackerServer, null);
	}
}
*/
