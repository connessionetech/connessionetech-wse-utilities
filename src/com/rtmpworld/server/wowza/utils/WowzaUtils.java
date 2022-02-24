package com.rtmpworld.server.wowza.utils;


import com.rtmpworld.server.wowza.enums.StreamingProtocols;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.httpstreamer.model.IHTTPStreamerSession;
import com.wowza.wms.rtp.model.RTPSession;
import com.wowza.wms.rtp.model.RTPStream;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.webrtc.model.WebRTCSession;

public class WowzaUtils {
	
	
	public static void terminateSession(IApplicationInstance instance, IMediaStream stream)
	{
		StreamingProtocols protocol = WowzaUtils.getClientProtocol(stream);
		
		switch(protocol)
		{
			case RTMP:
				stream.getClient().setShutdownClient(true);
			break;
			
			case RTSP:							
			case WEBRTC:
			case SRT:
				RTPSession rtpSession = stream.getRTPStream().getSession();
				instance.getVHost().getRTPContext().shutdownRTPSession(rtpSession);
			break;
			
			case HTTP:
				IHTTPStreamerSession httpSession = stream.getHTTPStreamerSession();
				httpSession.rejectSession();
		        httpSession.shutdown();
				break;
				
			case UNKNOWN:
				default:
					
				break;
		}
	}
	
	
	
	public static void terminateSession(IApplicationInstance instance, IClient client)
	{
		client.rejectConnection();
		client.setShutdownClient(true);
	}
	
	
	public static void terminateSession(IApplicationInstance instance, RTPSession session)
	{
		StreamingProtocols protocol = WowzaUtils.getClientProtocol(session);
		
		if(protocol == StreamingProtocols.RTSP || protocol == StreamingProtocols.SRT)
		{		
			instance.getVHost().getRTPContext().shutdownRTPSession(session);
		}
		else if(protocol == StreamingProtocols.WEBRTC)
		{
			WebRTCSession webRTCSession = (session).getWebRTCSession();
			instance.getVHost().getWebRTCContext().shutdownSession(webRTCSession.getSessionId());
		}	
	}
	
	
	public static void terminateSession(IApplicationInstance instance, IHTTPStreamerSession session)
	{
		session.rejectSession();
		session.shutdown();
	}
	
	
	public static StreamingProtocols getClientProtocol(IMediaStream stream)
	{
		IClient client = stream.getClient();
		
		if(client != null)
		{
			if(client.getUri().contains("rtmp") || (client.getFlashVer() != null && client.getFlashVer().length() > 0) || (client.getProtocol() == 1 || client.getProtocol() == 3))
			{
				return StreamingProtocols.RTMP;
			}
			else
			{
				return StreamingProtocols.UNKNOWN;
			}
		}
		
		RTPStream rtpStream = stream.getRTPStream();
		if (rtpStream != null)
		{				
			RTPSession rtpSession = rtpStream.getSession();
			if (rtpSession != null)
			{
				if(rtpSession.isWebRTC())
				{
					return StreamingProtocols.WEBRTC;
				}
				if(rtpStream.isSRT())
				{
					return StreamingProtocols.SRT;
				}
				else
				{
					return StreamingProtocols.RTSP;
				}
			}
		}
		
		IHTTPStreamerSession httpStreamerSession = stream.getHTTPStreamerSession();
		if (httpStreamerSession != null)
		{
			return StreamingProtocols.HTTP;
		}
		
		return StreamingProtocols.UNKNOWN;
	}
	
	
	
	public static StreamingProtocols getClientProtocol(RTPSession rtpSession)
	{
		if (rtpSession != null)
		{
			if(rtpSession.isWebRTC())
			{
				return StreamingProtocols.WEBRTC;
			}
			if(rtpSession.isWebRTC())
			{
				return StreamingProtocols.WEBRTC;
			}
			else
			{
				return StreamingProtocols.RTSP;
			}
		}
		
		return StreamingProtocols.UNKNOWN;
	}
	
	
	
	public static StreamingProtocols getClientProtocol(IHTTPStreamerSession session)
	{
		if (session != null)
		{
			return StreamingProtocols.HTTP;
		}
		
		return StreamingProtocols.UNKNOWN;
	}
	
	
	
	public static StreamingProtocols getClientProtocol(IClient client)
	{
		if(client != null)
		{
			if(client.getUri().contains("rtmp") || (client.getFlashVer() != null && client.getFlashVer().length() > 0) || (client.getProtocol() == 1 || client.getProtocol() == 3))
			{
				return StreamingProtocols.RTMP;
			}
		}
		
		return StreamingProtocols.UNKNOWN;
	}
	
	
	

	public static boolean isRTMPClient(IClient client)
	{
		if(client != null)
		{
			if((client.getFlashVer() != null && client.getFlashVer().length() > 0) || (client.getProtocol() == 1 || client.getProtocol() == 3))
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public static boolean isRTPStream(IMediaStream stream)
	{
		RTPStream rtpStream = stream.getRTPStream();
		if (rtpStream != null)
		{				
			RTPSession rtpSession = rtpStream.getSession();
			if (rtpSession != null)
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	
	public static boolean supportsDigestAuthentication(IClient client)
	{
		if(client.getQueryStr() == "" || client.getQueryStr().length()<1)
		{
			if(client.getFlashVer().contains("FMLE") || client.getFlashVer().contains("FMS") || client.getFlashVer().contains("compatible"))
			{
				return true;
			}
		}
				
		return false;
	}
}
