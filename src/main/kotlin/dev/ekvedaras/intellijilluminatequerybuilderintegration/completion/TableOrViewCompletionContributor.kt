package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns

class TableOrViewCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            TableOrViewCompletionProvider()
        );
    }
}