package no.sandramoen.libgdx33.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;

public class RadiationZone {

    private Polygon bounds;
    private float radius;
    private int numSides;
    private float centerX, centerY;

    private float[] baseRadii;     // Base radius for each vertex
    private float[] angleOffsets;  // Phase offset per vertex for morphing
    private float morphAmplitude = 15f;  // Radius oscillation amount
    private float morphSpeed = 2f;       // Oscillation speed
    private float radiusX, radiusY;


    public RadiationZone(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;

        radiusX = MathUtils.random(40f, 200f);
        radiusY = MathUtils.random(40f, 200f);

        numSides = MathUtils.random(12, 24);

        baseRadii = new float[numSides];
        angleOffsets = new float[numSides];

        for (int i = 0; i < numSides; i++) {
            baseRadii[i] = 1f; // normalized radius
            angleOffsets[i] = MathUtils.random(0f, MathUtils.PI2);
        }

        updateVertices(0f);
    }


    /** Call this every frame to update the polygon's shape */
    public void update(float elapsedTime) {
        updateVertices(elapsedTime);
    }


    /** Updates polygon vertices with oscillation around the center point */
    private void updateVertices(float elapsedTime) {
        float[] vertices = new float[numSides * 2];
        for (int i = 0; i < numSides; i++) {
            float morphFactor = 1f + (morphAmplitude / radiusX) * (float)Math.sin(morphSpeed * elapsedTime + angleOffsets[i]);
            float angle = (float)(i * 2 * Math.PI / numSides);

            vertices[2 * i] = centerX + radiusX * baseRadii[i] * morphFactor * (float)Math.cos(angle);
            vertices[2 * i + 1] = centerY + radiusY * baseRadii[i] * morphFactor * (float)Math.sin(angle);
        }
        bounds = new Polygon(vertices);
    }


    public void draw(ShapeRenderer shape_renderer) {
        float[] vertices = bounds.getTransformedVertices();

        // --- First pass: draw inside scratch lines ---
        Gdx.gl.glLineWidth(5f);
        shape_renderer.begin(ShapeRenderer.ShapeType.Line);
        shape_renderer.setColor(new Color(0f, 0.3f, 0f, 1f)); // dark green

        float spacing = 10f; // distance between scratch lines
        float angle = 45f;   // angle in degrees

        // Get bounding box of polygon
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        for (int i = 0; i < vertices.length; i += 2) {
            float x = vertices[i];
            float y = vertices[i + 1];
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float length = (float) Math.sqrt((maxX - minX)*(maxX - minX) + (maxY - minY)*(maxY - minY));
        float centerX = (minX + maxX) / 2f;
        float centerY = (minY + maxY) / 2f;

        for (float offset = -length; offset <= length; offset += spacing) {
            float startX = minX;
            float startY = minY + offset;
            float endX = maxX;
            float endY = minY + offset;

            // Rotate start and end points around center
            float rotatedStartX = cos * (startX - centerX) - sin * (startY - centerY) + centerX;
            float rotatedStartY = sin * (startX - centerX) + cos * (startY - centerY) + centerY;
            float rotatedEndX = cos * (endX - centerX) - sin * (endY - centerY) + centerX;
            float rotatedEndY = sin * (endX - centerX) + cos * (endY - centerY) + centerY;

            int segments = 20;
            float prevX = rotatedStartX;
            float prevY = rotatedStartY;
            boolean prevInside = bounds.contains(prevX, prevY);

            for (int i = 1; i <= segments; i++) {
                float t = i / (float) segments;
                float currX = rotatedStartX + t * (rotatedEndX - rotatedStartX);
                float currY = rotatedStartY + t * (rotatedEndY - rotatedStartY);
                boolean currInside = bounds.contains(currX, currY);

                if (prevInside && currInside) {
                    shape_renderer.line(prevX, prevY, currX, currY);
                }
                prevX = currX;
                prevY = currY;
                prevInside = currInside;
            }
        }
        shape_renderer.end();

        // --- Second pass: draw polygon border lines and center-to-vertex spokes ---
        Gdx.gl.glLineWidth(20f);
        shape_renderer.begin(ShapeRenderer.ShapeType.Line);

        shape_renderer.setColor(Color.GREEN);
        shape_renderer.polygon(vertices);

        shape_renderer.end();
    }


    public boolean overlaps(Polygon actorPolygon, Camera camera) {
        float[] worldVertices = actorPolygon.getTransformedVertices();
        float[] screenVertices = new float[worldVertices.length];
        for (int i = 0; i < worldVertices.length; i += 2) {
            float wx = worldVertices[i];
            float wy = worldVertices[i + 1];
            Vector3 screenCoords = camera.project(new Vector3(wx, wy, 0));
            screenVertices[i] = screenCoords.x;
            screenVertices[i + 1] = screenCoords.y;
        }
        Polygon screenPolygon = new Polygon(screenVertices);
        return Intersector.overlapConvexPolygons(this.bounds, screenPolygon);
    }


    public Polygon getBounds() {
        return bounds;
    }
}
