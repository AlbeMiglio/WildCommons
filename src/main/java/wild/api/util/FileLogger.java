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
package wild.api.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import lombok.NonNull;

import org.bukkit.plugin.Plugin;

/**
 * Classe utilizzata per salvataggi non frequenti.
 */
public class FileLogger {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MM/yy - HH:mm:ss]");
	
	private File file;
	private Plugin plugin;
	
	public FileLogger(@NonNull Plugin plugin, @NonNull File file) {
		this.plugin = plugin;
		this.file = file;
	}
	
	public FileLogger(Plugin plugin, @NonNull String fileName) {
		this(plugin, new File(plugin.getDataFolder(), fileName));
	}
	
	public void log(String message) {
		log(message, null);
	}
	
	public void log(String message, Throwable t) {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.WARNING, "Could not create log file", e);
				return;
			}
		}
		
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			
			Date now = new Date();
			for (String line : message.split("\n")) {
				out.println(dateFormat.format(now) + " " + line);
			}
			if (t != null) {
				t.printStackTrace(out);
			}
			 
		} catch (IOException e) {
			plugin.getLogger().log(Level.WARNING, "Could not append message to log file", e);
			return;
		}
	}

}
