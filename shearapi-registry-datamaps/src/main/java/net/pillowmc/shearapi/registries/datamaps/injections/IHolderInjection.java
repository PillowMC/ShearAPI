package net.pillowmc.shearapi.registries.datamaps.injections;

public interface IHolderInjection<T> {
    <A> A getData(net.neoforged.neoforge.registries.datamaps.DataMapType<T, A> type);
}
