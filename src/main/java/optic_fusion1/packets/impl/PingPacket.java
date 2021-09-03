/*
 * Copyright (C) 2021 Optic_Fusion1
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
package optic_fusion1.packets.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import optic_fusion1.packets.IPacket;

import java.io.IOException;

public class PingPacket implements IPacket {

    private long systemTime;

    public PingPacket() {
    }

    public PingPacket(final long systemTime) {
        this.systemTime = systemTime;
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeLong(this.systemTime);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream) throws IOException {
        this.systemTime = dataInputStream.readLong();
    }

    public long getSystemTime() {
        return this.systemTime;
    }

}
