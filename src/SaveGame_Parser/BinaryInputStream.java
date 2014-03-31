/*
 * This file is part of the Skyrim Savegame Utilities Project: https://code.google.com/p/skyrim-savegame-utilities/
 *  
 * Copyright (c) 2014 Rick Falck
 *  
 * This code is licensed under the terms of the Apache License Version 2.0.
 * You may use this code according to the license.
 *
 * The terms of the license can be found in the root directory of this project's repository as well as at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these Licenses is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See each License for the specific language governing permissions and
 * limitations under that License.
 */

package SaveGame_Parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
/**
 * @author trira
 * @version 1.0
 */
public class BinaryInputStream {
    private FileInputStream stream;

    private byte[] buffer = new byte[32];

    public BinaryInputStream(String path) throws FileNotFoundException {
        this.stream = new FileInputStream(new File(path));
    }

    public int nextByte() throws IOException {
        return this.stream.read() & 0xFF;
    }

    public int nextShort() throws IOException {
        this.stream.read(this.buffer, 0, 2);
        return (this.buffer[1] & 0xFF) << 8 | this.buffer[0] & 0xFF;
    }

    public int nextInt() throws IOException {
        this.stream.read(this.buffer, 0, 4);
        return ((this.buffer[3] & 0xFF) << 24 | (this.buffer[2] & 0xFF) << 16
                | (this.buffer[1] & 0xFF) << 8 | this.buffer[0] & 0xFF);
    }

    public RefID nextRefID() throws IOException {
        // RefID are big endian despite everything else being little endian.
        this.stream.read(this.buffer, 0, 3);
        return new RefID((this.buffer[0] & 0xFF) << 16 | (this.buffer[1] & 0xFF) << 8
                | this.buffer[2] & 0xFF);
    }

    public String nextString() throws IOException {
        int len = this.nextShort();
        // Make sure buffer is wide enough
        if (len > this.buffer.length) {
            this.buffer = new byte[len];
        }
        this.stream.read(this.buffer, 0, len);
        return new String(this.buffer, 0, len);
    }

    public byte[] nextCompressedSegment(int length) throws IOException, DataFormatException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] tbuffer = new byte[1024];
        byte[] in = new byte[length];
        Inflater inf = new Inflater();
        // Initialize inflater
        this.stream.read(in);
        inf.setInput(in);
        // Decompress data.
        while (!inf.finished()) {
            int len = inf.inflate(buffer);
            out.write(tbuffer, 0, len);
        }
        inf.end();
        return out.toByteArray();
    }

    public long pos() throws IOException {
        return this.stream.getChannel().position();
    }

    public void skip(long n) throws IOException {
        this.stream.skip(n);
    }

    public void close() throws IOException {
        this.stream.close();
    }

}
