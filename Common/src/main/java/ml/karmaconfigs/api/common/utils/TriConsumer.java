package ml.karmaconfigs.api.common.utils;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T, U, V> {

    void accept(T paramT, U paramU, V paramV);

    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            accept((T) a, (U) b, (V) c);
            after.accept(a, b, c);
        };
    }
}
