package dataOld;

public record DvojicaOld<A extends Comparable<A>, B extends Comparable<B>>(A prva,
                                                                           B druha) implements Comparable<DvojicaOld<A, B>> {

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DvojicaOld<?, ?> dvojica = (DvojicaOld<?, ?>) obj;
        return prva.equals(dvojica.prva) && druha.equals(dvojica.druha);
    }

    @Override
    public int compareTo(DvojicaOld<A, B> other) {
        int cmp = this.prva.compareTo(other.prva);
        if (cmp != 0) {
            return cmp;
        }
        return this.druha.compareTo(other.druha);
    }
}