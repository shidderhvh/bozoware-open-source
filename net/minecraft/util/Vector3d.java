package net.minecraft.util;

public class Vector3d
{
    public double field_181059_a;
    public double field_181060_b;
    public double field_181061_c;

    public Vector3d()
    {
        this.field_181059_a = this.field_181060_b = this.field_181061_c = 0.0D;
    }

    public Vector3d(float v, float v1, float v2) {
        field_181059_a = v;
        field_181060_b = v1;
        field_181061_c = v2;
    }

    public Vector3d(double minX, double minY, double minZ) {
        field_181059_a = minX;
        field_181060_b = minY;
        field_181061_c = minZ;
    }
}
