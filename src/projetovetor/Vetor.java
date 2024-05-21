package projetovetor;

public class Vetor {

    private Double x;
    private Double y;
    private Double z;

    // Construtor para vetores 2D
    public Vetor(Double x, Double y) {
        this.x = x;
        this.y = y;
        this.z = null; // Define z como null para vetores 2D
    }

    // Construtor para vetores 3D
    public Vetor(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Getters para as coordenadas
    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
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
        if (v1.z == null) {
            v1.z = 0.0;
        }
        if (v2.z == null) {
            v2.z = 0.0;
        }
        double x = v1.y * v2.z - v1.z * v2.y;
        double y = v1.z * v2.x - v1.x * v2.z;
        double z = v1.x * v2.y - v1.y * v2.x;
        return new Vetor(x, y, z);
    }

    // Formatação da string de saída para vetores
    @Override
    public String toString() {
        if (z == null) {
            return "(" + x + ", " + y + ")";
        } else {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }
}
