/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package wild.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.PluginDescriptionFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.Cleanup;

public class GenericUtils {
	
	public static JsonElement readJsonElementFromURL(String url) throws IOException, MalformedURLException, SocketTimeoutException {
		BufferedReader reader = null;
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.addRequestProperty("User-Agent", "Minecraft");
			connection.setConnectTimeout(5000); //tempo massimo per connettersi al sito
			connection.setReadTimeout(8000); //tempo massimo per leggere l'url
			reader = new BufferedReader(new InputStreamReader(connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream()));
			
			JsonParser parser = new JsonParser();
			return parser.parse(reader);

		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	public static String getPluginYmlName(File file) throws IOException {
		@Cleanup JarFile jar = new JarFile(file);
		JarEntry entry = jar.getJarEntry("plugin.yml");
		
		if (entry == null) {
			throw new FileNotFoundException("Jar does not contain plugin.yml");
		}
		
		@Cleanup InputStream stream = jar.getInputStream(entry);
		
	    try {
	      PluginDescriptionFile description = new PluginDescriptionFile(stream);
	      return description.getName();
	      
	    } catch (Exception ex) {
	    	throw new IOException(ex);
	    }
	}

}
