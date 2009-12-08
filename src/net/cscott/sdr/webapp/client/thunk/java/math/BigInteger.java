package java.math;

/** Hacked-up BigInteger which does the least thing that will work for
 * {@link net.cscott.sdr.util.Fraction}. */
public class BigInteger {
    long value;
    private BigInteger(long value) { this.value = value; }
    public static BigInteger valueOf(long value) {
	return new BigInteger(value);
    }
    public BigInteger multiply(BigInteger v) {
	return BigInteger.valueOf(this.value * v.value);
    }
    public BigInteger divide(BigInteger v) {
	return BigInteger.valueOf(this.value / v.value);
    }
    public BigInteger add(BigInteger v) {
	return BigInteger.valueOf(this.value + v.value);
    }
    public BigInteger subtract(BigInteger v) {
	return BigInteger.valueOf(this.value - v.value);
    }
    public BigInteger mod(BigInteger v) {
	return BigInteger.valueOf(this.value % v.value);
    }
    public int bitLength() { return 31; /* JUST A HACK! */ }
    public int intValue() { return (int) this.value; }
}
