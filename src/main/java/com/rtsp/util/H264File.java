package com.rtsp.util;

import java.io.*;

/**
 * read h264 file
 * @author gzcheng
 *
 */
public class H264File {
	/**
	 * file data
	 */
	public byte[] fileData;
	public int readPos = 0;
    public boolean havHead; //是否有数据头 00 00 00 01 或 00 00 01  没有直接返回fileData

	public boolean open(String filePath) {
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			int fileLen = (int)file.length();
			fileData = new byte[fileLen];
			fis.read(fileData);
			fis.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}


	public byte[] readNalu() {
		if(readPos >= fileData.length)
			return null;
		int pos = readPos;
		int bufLen = fileData.length;
		int naluPos = -1;
		while(pos < (bufLen-2)) {
			if(naluPos == -1) {
				if(fileData[pos] == 0x00 && fileData[pos+1] == 0x00) {
					if(fileData[pos+2] == 0x01) {
						naluPos = pos + 3;
						pos += 3;
                        //有数据头
                        havHead = true;
						continue;
					}
					else if(fileData[pos+2] == 0x00 && (pos+3) < bufLen && fileData[pos+3] == 0x01) {
						naluPos = pos + 4;
						pos += 4;
                        //有数据头
                        havHead = true;
						continue;
					}
				}
				pos++;
				continue;
			}
			//找到下一个nalu开头
			if(fileData[pos] == 0x00 && fileData[pos+1] == 0x00) {
				if(fileData[pos+2] == 0x01) {
					int naluLen = pos - naluPos;
					byte[] nalu = new byte[naluLen];
					System.arraycopy(fileData, naluPos, nalu, 0, naluLen);
					readPos = pos;
                    //有数据头
                    havHead = true;
					return nalu;
				}
				else if(fileData[pos+2] == 0x00 && (pos+3) < bufLen && fileData[pos+3] == 0x01) {
					int naluLen = pos - naluPos;
					byte[] nalu = new byte[naluLen];
					System.arraycopy(fileData, naluPos, nalu, 0, naluLen);
					readPos = pos;
                    //有数据头
                    havHead = true;
					return nalu;
				}
			}
			pos++;
			continue;
		}
		if(naluPos == -1 && havHead) {
            return null;
        }
        if(naluPos == -1 && !havHead) {
            readPos = 0;
            return fileData;
        }
		//最后一个
		int naluLen = bufLen - naluPos;
		byte[] nalu = new byte[naluLen];
		System.arraycopy(fileData, naluPos, nalu, 0, naluLen);
		readPos = bufLen;
		return nalu;
	}


}
