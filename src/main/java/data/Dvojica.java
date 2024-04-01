package data;

public record Dvojica<A extends Comparable<A>, B extends Comparable<B>>(A prva,
                                                                        B druha) implements Comparable<Dvojica<A, B>> {

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dvojica<?, ?> dvojica = (Dvojica<?, ?>) obj;
        return prva.equals(dvojica.prva) && druha.equals(dvojica.druha);
    }

    @Override
    public int compareTo(Dvojica<A, B> other) {
        int cmp = this.prva.compareTo(other.prva);
        if (cmp != 0) {
            return cmp;
        }
        return this.druha.compareTo(other.druha);
    }
}