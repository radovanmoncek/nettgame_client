package cz.radovanmoncek.client.ship.parents.builders;

public interface Builder<T> {

    T build();

    Builder<T> reset();
}
