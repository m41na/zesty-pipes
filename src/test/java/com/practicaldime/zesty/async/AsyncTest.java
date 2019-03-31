package com.practicaldime.zesty.async;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Test;

public class AsyncTest {

	@Test
	public void testAsyncStuff() {
		Integer value = 5;
		Supplier<Integer> start = () -> value;
//		Function<Integer, Integer> trip = (n) -> {
//			throw new RuntimeException("tripping");
//		};
		Function<Integer, Integer> square = (n) -> n * n;

		Function<Integer, Integer> half = (n) -> n / 2;
		Function<List<Integer>, List<Integer>> even = (list) -> list.stream().filter(n -> n % 2 == 0)
				.collect(Collectors.toList());
		Function<List<Integer>, List<Integer>> triple = (list) -> list.stream().map(n -> n * 3)
				.collect(Collectors.toList());
		Function<Integer, List<Integer>> genlist = (size) -> {
			Integer[] ints = new Integer[size];
			for (int i = 0; i < size; i++) {
				ints[i] = i;
			}
			return Arrays.asList(ints);
		};
		Consumer<List<Integer>> results = (list) -> list.stream().forEach(n -> System.out.println(n));
		BiFunction<List<Integer>, Throwable, List<Integer>> onError = new BiFunction<List<Integer>, Throwable, List<Integer>>() {

			@Override
			public List<Integer> apply(List<Integer> res, Throwable th) {
				if (res != null) {
					System.out.println("****handled " + res);
					return res;
				} else {
					System.out.println(th.getMessage());
					return Collections.emptyList();
				}
			}
		};

		// now play with these
		CompletableFuture.supplyAsync(start).thenApply(square::apply)
//		.thenApply(trip::apply)
				.exceptionally(th -> {
					System.out.println("****exception " + th.getMessage());
					return 100;
				}).thenApply(half::apply).thenApply(genlist::apply).thenApply(even::apply).thenApply(triple::apply)
				// .thenCompose(n->factor(n))
				.handle(onError::apply).thenAccept(results::accept);
	}

	@Test
	public void testAsyncStuff2() {
		Integer start = 100;
		Function<Double, Double> trip = (n) -> {
			throw new RuntimeException("tripping");
		};
		CompletableFuture<Double> val = new CompletableFuture<>();
		factor(start)
		.thenApply(trip)
		.handle((res, th) ->{
			System.out.println(th.getMessage());
			val.completeExceptionally(th);
			return res;
		})
		.thenApply(n -> val.complete(n * 2))
		.thenAccept(System.out::println);
		
		Double res = val.isCompletedExceptionally()? 0.1 : val.join();
		System.out.println(res);
	}

	static CompletableFuture<Double> factor(Integer n) {
		CompletableFuture<Double> val = new CompletableFuture<>();
		Double res = 2 / Math.log10(n);
		val.complete(res);
		return val;
	}
}
