package net.cscott.sdr.anim;

import com.jme.math.*;

public class QTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        test(-1f,0,0,0,.7417227f,.67070687f,0,0.67070675f,-0.74172264f);
        test(-0.9877012f,0.10444529f,-0.115603514f,
                2.7908056E-4f,0.74363315f,0.67279655f,
                0.15635325f,0.6603793f,-0.7307397f);
        test(-0.95960325f,0.15303269f,-0.18045656f,
                0.021273915f,0.80862266f,0.76158494f,
                0.2805512f,0.568076f,-0.62243384f);
        test(-0.9063946f,0.36770543f,-0.24786337f,
                -0.108173415f,0.31893745f,0.9124022f,
                0.4083471f,0.87353975f,-0.3257084f);
        test(-0.9747772f,0.19721694f,-0.03884777f,
                -0.008699911f,0.23692809f,0.99724585f,
                0.2230103f,0.9512994f,-0.0631785f);
        test(-0.9641433f,-0.13261475f,-0.23101787f,
                -0.24161902f,0.78984094f,0.5551781f,
                0.10976411f,0.59880257f,-0.7990043f);
        test(-0.9624234f,-0.24886328f,-0.10471525f,
                -0.26489657f,0.7831659f,0.56719583f,
                -0.059759382f,0.56984055f,-0.8168988f);
    }
    
    static void test(float m00, float m01, float m02,
                     float m10, float m11, float m12,
                     float m20, float m21, float m22) {
        Quaternion q = new Quaternion();
        q.fromRotationMatrix(m00, m01, m02, m10, m11, m12, m20, m21, m22);
        
        q.normalize();
        
        Matrix3f m = q.toRotationMatrix();
        // compare
        float diff =
            d(m00,m.m00)+d(m01,m.m01)+d(m02,m.m02)+
            d(m10,m.m10)+d(m11,m.m11)+d(m12,m.m12)+
            d(m20,m.m20)+d(m21,m.m21)+d(m22,m.m22);
        if (diff > 0.1f) {
        System.err.println("DIFF: "+diff);
        float xd = m00*m00 + m10*m10 + m20*m20;
        float yd = m01*m01 + m11*m11 + m21*m21;
        float zd = m02*m02 + m12*m12 + m22*m22;
        float oxy = m00*m01 + m10*m11 + m20*m21;
        float oyz = m01*m02 + m11*m12 + m21*m22;
        float ozx = m02*m00 + m12*m10 + m22*m20;
        System.err.println("X:"+xd+" Y:"+yd+" Z:"+zd);
        System.err.println("OXY:"+oxy+" OYZ:"+oyz+" OZX:"+ozx);
        System.err.println(new Matrix3f(m00,m01,m02,m10,m11,m12,m20,m21,m22));
        System.err.println(m);
        }
    }
    private static float d(float a, float b) {
        float d = a-b;
        return d*d;
    }
}
