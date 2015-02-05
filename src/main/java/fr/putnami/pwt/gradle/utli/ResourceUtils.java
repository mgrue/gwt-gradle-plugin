/**
 * This file is part of pwt.
 *
 * pwt is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * pwt is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with pwt. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package fr.putnami.pwt.gradle.utli;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class ResourceUtils {
	private ResourceUtils() {
	}

	public static File ensureDir(File parent, String path) {
		File dir = new File(parent, path);
		dir.mkdirs();
		return dir;
	}

	public static File copy(String resourcePath, File targetDir, String targetName)
		throws IOException {
		return copy(resourcePath, targetDir, targetName, null);
	}

	public static File copy(String resourcePath, File targetDir, String targetName, Map<String, String> model)
		throws IOException {
		InputStream input = ResourceUtils.class.getResourceAsStream(resourcePath);
		// InputStreamReader in = new InputStreamReader(input, Charsets.UTF_8);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ByteStreams.copy(input, out);

		String template = new String(out.toByteArray());
		// Resources.toString(url, Charsets.UTF_8);

		if (model != null) {
			for (Map.Entry<String, String> entry : model.entrySet()) {
				template = template.replace(entry.getKey(), entry.getValue());
			}
		}

		File outFile = new File(targetDir, targetName);
		FileWriter writer = new FileWriter(outFile);
		writer.write(template);
		writer.close();
		return outFile;
	}

}
