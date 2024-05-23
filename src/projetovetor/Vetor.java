package projetovetor;

public class Vetor {

    private Double x;
    private Double y;
    private Double z;
    private boolean is2D;

    // Construtor para vetor 2D
    public Vetor(Double x, Double y) {
        this.x = x;
        this.y = y;
        this.z = 0.0;
        this.is2D = true; // Define como 2D
    }

    // Construtor para vetor 3D
    public Vetor(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.is2D = false; // Define como 3D
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public boolean is2D() {
        return is2D;
    }

    // Calcula a magnitude do vetor
    public double calcularMagnitude() {
        return Math.sqrt(x * x + y * y + (z != null ? z * z : 0));
    }

    // Calcula o produto escalar entre dois vetores
    public static double calcularProdutoEscalar(Vetor v1, Vetor v2) {
        return v1.x * v2.x + v1.y * v2.y + (v1.z != null && v2.z != null ? v1.z * v2.z : 0);
    }

    // Calcula o ângulo entre dois vetores
    public static double calcularAngulo(Vetor v1, Vetor v2) {
        double produtoEscalar = calcularProdutoEscalar(v1, v2);
        double magnitudes = v1.calcularMagnitude() * v2.calcularMagnitude();
        return Math.toDegrees(Math.acos(produtoEscalar / magnitudes));
    }

    // Calcula o produto vetorial entre dois vetores
    public static Vetor calcularProdutoVetorial(Vetor v1, Vetor v2) {
        double x = v1.y * v2.z - v1.z * v2.y;
        double y = v1.z * v2.x - v1.x * v2.z;
        double z = v1.x * v2.y - v1.y * v2.x;
        return new Vetor(x, y, z);
    }

    public static Vetor calcularVetorOrtogonal(Vetor v1, Vetor v2) {

        return new Vetor(-v2.y, v1.x, 0.0);
    }

    @Override
    public String toString() {
        // Formata a saída dependendo do tipo de vetor (2D ou 3D)
        return is2D ? String.format("(%.1f, %.1f)", x, y) : String.format("(%.1f, %.1f, %.1f)", x, y, z);
    }
}
