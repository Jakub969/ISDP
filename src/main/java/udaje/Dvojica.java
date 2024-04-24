package udaje;

public record Dvojica<A extends Comparable<A>, B extends Comparable<B>>(A prvyPrvok,
                                                                           B druhyPrvok) implements Comparable<Dvojica<A, B>> {

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dvojica<?, ?> dvojica = (Dvojica<?, ?>) obj;
        return prvyPrvok.equals(dvojica.prvyPrvok) && druhyPrvok.equals(dvojica.druhyPrvok);
    }

    @Override
    public int compareTo(Dvojica<A, B> other)
    {
        int cmp = this.prvyPrvok.compareTo(other.prvyPrvok);
        if (cmp != 0) {
            return cmp;
        }
        return this.druhyPrvok.compareTo(other.druhyPrvok);
    }
}