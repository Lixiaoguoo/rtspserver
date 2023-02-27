package com.rtsp.udp;

import com.rtsp.handler.ProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;

/**
 */
public class UDPSocket implements Runnable {
    protected static final Logger log = LoggerFactory.getLogger(UDPSocket.class);
	
	protected DatagramSocket m_Socket;
	protected ProtocolHandler m_ProtocolHandler;
	protected Thread m_RecvThread;
	protected volatile boolean m_bRunnable;
	/**
	 * UDP pack size
	 */
	public final int MAX_UDPPACK_SIZE = 8192;
	protected int m_nUDPPackSize;
	
	public UDPSocket() {
		m_bRunnable = false;
		m_nUDPPackSize = MAX_UDPPACK_SIZE;
	}
	/**
	 * set handler
	 */
	public void setProtocolHandler(ProtocolHandler handler) {
		m_ProtocolHandler = handler;
	}
	public ProtocolHandler GetProtocolHandler() {
		return m_ProtocolHandler;
	}
	/**
	 * set max UDP pack size
	 */
	public void SetUDPPackSize(int nPackSize) {
		m_nUDPPackSize = nPackSize;
	}
	/**
	 * open
	 */
	public boolean open(String strLocalIP, int nLocalPort) {
		InetSocketAddress sa = new InetSocketAddress(strLocalIP, nLocalPort);
		return open(sa);
	}
	public boolean open(int nLocalPort) {
		InetSocketAddress sa = new InetSocketAddress(nLocalPort);
		return open(sa);
	}
	public boolean open(SocketAddress sa) {
		close();
		
		m_bRunnable = true;
		try {
			//open socket
			m_Socket = new DatagramSocket(sa);
			m_Socket.setReceiveBufferSize(65535);
			//create thread
			m_RecvThread = new Thread(this, "UDPSocketThread");
			m_RecvThread.start();
		} 
		catch(SocketException e) {
			log.error(e.getMessage(), e);
			return false;
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	/**
	 * close
	 */
	public void close() {
		m_bRunnable = false;
		if(m_Socket != null)
			m_Socket.close();
		if(m_RecvThread != null) {
			try {
				m_RecvThread.join(2000); 
			}
			catch(Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	/**
	 * run
	 */
	public void run() {
		byte[] byBuffer = new byte[m_nUDPPackSize];
		DatagramPacket dp = new DatagramPacket(byBuffer, byBuffer.length);
		
		while(m_bRunnable) {
			try {
				m_Socket.receive(dp);
				if(m_ProtocolHandler != null) {
					m_ProtocolHandler.handlePack(dp.getData(), dp.getLength(), 
							dp.getSocketAddress());
				}
			}
			catch(Exception e) {
				if(m_bRunnable)
					log.error(e.getMessage(), e);
			}
		}
	}
	/**
	 * send data
	 */
	public boolean sendData(byte[] buf, int offset, int length, 
			SocketAddress address) {
		if(m_Socket == null)
			return false;
		DatagramPacket dp = new DatagramPacket(buf, offset, length, address);
		try {
			m_Socket.send(dp); 
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	public boolean sendData(byte[] buf, int offset, int length, 
			String strServerIP, int nServerPort) {
		InetSocketAddress address = null;
		try {
			address = new InetSocketAddress(strServerIP, nServerPort);
		}
		catch(Exception e) {
			return false;
		}
		return sendData(buf, offset, length, address);
	}
	public boolean sendData(byte[] buf, int length, SocketAddress address) {
		return sendData(buf, 0, length, address);
	}
	public boolean sendData(byte[] buf, int length, String strServerIP, 
			int nServerPort) {
		return sendData(buf, 0, length, strServerIP, nServerPort);
	}
}
