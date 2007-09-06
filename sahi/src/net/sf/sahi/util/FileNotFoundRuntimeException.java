/**
 * Sahi - Web Automation and Test Tool
 *
 * Copyright  2006  V Narayan Raman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sahi.util;

public class FileNotFoundRuntimeException extends RuntimeException {

	public FileNotFoundRuntimeException() {
		super();
	}

	public FileNotFoundRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FileNotFoundRuntimeException(String arg0) {
		super(arg0);
	}

	public FileNotFoundRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
