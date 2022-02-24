package com.rtmpworld.server.wowza.decorators;



import com.rtmpworld.server.wowza.enums.StreamingProtocols;
import com.rtmpworld.server.wowza.utils.WowzaUtils;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.httpstreamer.model.IHTTPStreamerSession;
import com.wowza.wms.rtp.model.RTPSession;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.webrtc.model.WebRTCSession;

public class StreamingSessionTarget {
	
	private Object target;
	private StreamingProtocols protocol;
	private IApplicationInstance appInstance;
	

	
	public StreamingSessionTarget(IApplicationInstance appInstance, IMediaStream target)
	{
		this.target = target;
		this.appInstance = appInstance;
		this.protocol = WowzaUtils.getClientProtocol(target);
	}
	
	
	public StreamingSessionTarget(IApplicationInstance appInstance, IClient target)
	{
		this.target = target;
		this.appInstance = appInstance;
		this.protocol = WowzaUtils.getClientProtocol(target);
	}
	
	
	public StreamingSessionTarget(IApplicationInstance appInstance, RTPSession target)
	{
		this.target = target;
		this.appInstance = appInstance;
		this.protocol = WowzaUtils.getClientProtocol(target);
	}
	
	
	public StreamingSessionTarget(IApplicationInstance appInstance, IHTTPStreamerSession target)
	{
		this.target = target;
		this.appInstance = appInstance;
		this.protocol = WowzaUtils.getClientProtocol(target);
	}
	
	
	public String getIPAddress()
	{
		switch(protocol)
		{
			case RTMP:
				IClient client = (IClient)((IMediaStream) target).getClient();
				return client.getIp();
			
			case RTSP:							
			case WEBRTC:
			case SRT:
				RTPSession rtpSession = (RTPSession) ((IMediaStream) target).getRTPStream().getSession();
				return rtpSession.getIp();
			
			case HTTP:
				IHTTPStreamerSession httpSession = (IHTTPStreamerSession) ((IMediaStream) target).getHTTPStreamerSession();
				return httpSession.getIpAddress();
				
			case UNKNOWN:
				default:
				return null;
		}
	}
	
	
	public void terminateSession()
	{
		switch(protocol)
		{
			case RTMP:
				IClient client = (IClient) target;
				client.rejectConnection();
				client.setShutdownClient(true);
			break;
			
			case WEBRTC:
				WebRTCSession webRTCSession = ((RTPSession) target).getWebRTCSession();
				appInstance.getVHost().getWebRTCContext().shutdownSession(webRTCSession.getSessionId());
				break;
			case RTSP:
			case SRT:
				RTPSession rtpSession = (RTPSession) target;
				appInstance.getVHost().getRTPContext().shutdownRTPSession(rtpSession);
			break;
			
			case HTTP:
				IHTTPStreamerSession httpSession = (IHTTPStreamerSession) target;
				httpSession.rejectSession();
		        httpSession.shutdown();
				break;
				
			case UNKNOWN:
				default:					
				break;
		}
	}
}
