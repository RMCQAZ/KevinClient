package kevin.utils.render.GL;

import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

import static org.lwjgl.opengl.GL11.*;

public class DirectTessCallback extends GLUtessellatorCallbackAdapter {
    public final static DirectTessCallback INSTANCE = new DirectTessCallback();

    @Override
    public void begin(int type) {
        glBegin(type);
    }

    @Override
    public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
        double[] combined = new double[6];
        combined[0] = coords[0];
        combined[1] = coords[1];
        combined[2] = coords[2];
        combined[3] = 1;
        combined[4] = 1;
        combined[5] = 1;

        for (int i=0;i < outData.length;i++) {
            outData[i] = new VertexData(combined);
        }
    }

    public void end() {
        glEnd();
    }

    @Override
    public void vertex(Object vertexData) {
        VertexData vertex = (VertexData) vertexData;

        glVertex3f((float)vertex.data[0], (float)vertex.data[1], (float)vertex.data[2]);
    }
}
