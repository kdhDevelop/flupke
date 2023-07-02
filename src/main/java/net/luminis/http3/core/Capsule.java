/*
 * Copyright © 2023 Peter Doornbosch
 *
 * This file is part of Flupke, a HTTP3 client Java library
 *
 * Flupke is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Flupke is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.luminis.http3.core;

import net.luminis.quic.VariableLengthInteger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * https://www.rfc-editor.org/rfc/rfc9297.html#name-capsules
 */
public class Capsule {

    private long type;
    private long length;
    private byte[] value;

    public Capsule(long type, byte[] value) {
        this.type = type;
        this.length = value.length;
        this.value = value;
    }

    protected Capsule(long type, long length) {
        this.type = type;
        this.length = length;
        value = null;
    }

    public int write(OutputStream outputStream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8 + 8 + value.length);
        VariableLengthInteger.encode(type, buffer);
        VariableLengthInteger.encode(length, buffer);
        buffer.put(value);
        // Write to output stream in one operation, to avoid multiple data frames.
        outputStream.write(buffer.array(), 0, buffer.position());
        return buffer.position();
    }

    public long getType() {
        return type;
    }

    public long getLength() {
        return length;
    }

    public byte[] getData() {
        return value;
    }

    @Override
    public String toString() {
        return "Capsule[type=" + type + ", length=" + length + "]";
    }
}
