package ru.ya.practicum.shopper.domain.usecase

abstract class UseCase<in P, R> {
    abstract suspend operator fun invoke(params: P): R
}

abstract class NoParamsUseCase<R> {
    abstract suspend operator fun invoke(): R
}
