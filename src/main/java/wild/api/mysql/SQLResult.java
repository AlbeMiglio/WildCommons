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
package wild.api.mysql;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SQLResult implements AutoCloseable {
	
	@NonNull private final Statement statement;
	@NonNull private final ResultSet resultSet;
	
	@Override
	public void close() throws SQLException {
		statement.close();
	}

	public boolean absolute(int row) throws SQLException {
		return resultSet.absolute(row);
	}

	public void afterLast() throws SQLException {
		resultSet.afterLast();
	}

	public void beforeFirst() throws SQLException {
		resultSet.beforeFirst();
	}
	
	public int findColumn(String columnLabel) throws SQLException {
		return resultSet.findColumn(columnLabel);
	}

	public boolean first() throws SQLException {
		return resultSet.first();
	}

	public Array getArray(int columnIndex) throws SQLException {
		return resultSet.getArray(columnIndex);
	}

	public Array getArray(String columnLabel) throws SQLException {
		return resultSet.getArray(columnLabel);
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		return resultSet.getBoolean(columnIndex);
	}

	public boolean getBoolean(String columnLabel) throws SQLException {
		return resultSet.getBoolean(columnLabel);
	}

	public double getDouble(int columnIndex) throws SQLException {
		return resultSet.getDouble(columnIndex);
	}

	public double getDouble(String columnLabel) throws SQLException {
		return resultSet.getDouble(columnLabel);
	}

	public float getFloat(int columnIndex) throws SQLException {
		return resultSet.getFloat(columnIndex);
	}

	public float getFloat(String columnLabel) throws SQLException {
		return resultSet.getFloat(columnLabel);
	}

	public int getInt(int columnIndex) throws SQLException {
		return resultSet.getInt(columnIndex);
	}

	public int getInt(String columnLabel) throws SQLException {
		return resultSet.getInt(columnLabel);
	}

	public long getLong(int columnIndex) throws SQLException {
		return resultSet.getLong(columnIndex);
	}

	public long getLong(String columnLabel) throws SQLException {
		return resultSet.getLong(columnLabel);
	}

	public short getShort(int columnIndex) throws SQLException {
		return resultSet.getShort(columnIndex);
	}

	public short getShort(String columnLabel) throws SQLException {
		return resultSet.getShort(columnLabel);
	}

	public String getString(int columnIndex) throws SQLException {
		return resultSet.getString(columnIndex);
	}

	public String getString(String columnLabel) throws SQLException {
		return resultSet.getString(columnLabel);
	}
	
	public int getRow() throws SQLException {
		return resultSet.getRow();
	}

	public void insertRow() throws SQLException {
		resultSet.insertRow();
	}

	public boolean isAfterLast() throws SQLException {
		return resultSet.isAfterLast();
	}

	public boolean isBeforeFirst() throws SQLException {
		return resultSet.isBeforeFirst();
	}
	
	public boolean isFirst() throws SQLException {
		return resultSet.isFirst();
	}

	public boolean isLast() throws SQLException {
		return resultSet.isLast();
	}

	public boolean last() throws SQLException {
		return resultSet.last();
	}

	public boolean next() throws SQLException {
		return resultSet.next();
	}

	public boolean previous() throws SQLException {
		return resultSet.previous();
	}

	public boolean wasNull() throws SQLException {
		return resultSet.wasNull();
	}
	
	

}
