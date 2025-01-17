package protocol.io;

import java.io.InputStream;
import java.nio.ByteBuffer;
import lombok.SneakyThrows;

public class DataInputStream implements DataInput{
	
	private final java.io.DataInputStream delegate;
	
	public DataInputStream(InputStream in) {
        this.delegate = new java.io.DataInputStream(in);
    }


	@Override
	@SneakyThrows
	public ByteBuffer readNBytes(int n) {
		// TODO Auto-generated method stub
		return ByteBuffer.wrap(delegate.readNBytes(n));
	}

	@Override
	@SneakyThrows
	public byte peekByte() {
		// TODO Auto-generated method stub
		delegate.mark(1);
        byte value = delegate.readByte();
        delegate.reset();

        return value;

	}

	@Override
	@SneakyThrows
	public byte readSignedByte() {
		// TODO Auto-generated method stub
		return delegate.readByte();
	}

	@Override
	@SneakyThrows
	public short readSignedShort() {
		// TODO Auto-generated method stub
		return delegate.readShort();
	}

	@Override
	@SneakyThrows
	public int readSignedInt() {
		// TODO Auto-generated method stub
		return delegate.readInt();
	}

	@SneakyThrows
    @Override
    public long readSignedLong() {
        return delegate.readLong();
    }

}
