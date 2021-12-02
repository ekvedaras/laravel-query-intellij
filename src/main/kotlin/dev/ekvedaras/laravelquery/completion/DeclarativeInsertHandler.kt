// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtilEx
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager

class DeclarativeInsertHandler private constructor(
    private val ignoredCompletionChars: String,
    private val valueToInsert: String,
    private val autoPopup: Boolean,
) : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val editor = context.editor
        val completionChar = context.completionChar
        if (StringUtil.containsChar(ignoredCompletionChars, completionChar)) return
        val project = editor.project
        if (project != null) {
            val model = editor.caretModel
            if (isValueAlreadyHere(editor)) {
                model.moveToOffset(model.offset + valueToInsert.length)
            } else {
                EditorModificationUtilEx.insertStringAtCaret(editor, valueToInsert)
                PsiDocumentManager.getInstance(project).commitDocument(editor.document)
            }
            if (autoPopup) {
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null)
            }
        }
    }

    private fun isValueAlreadyHere(editor: Editor): Boolean {
        val startOffset = editor.caretModel.offset
        val document = editor.document
        val valueLength = valueToInsert.length
        return document.textLength >= startOffset + valueLength && document.getText(TextRange.create(startOffset, startOffset + valueLength)) == valueToInsert
    }

    class Builder {
        private var myIgnoredCharacters: String? = null
        private var myValueToInsert: String? = null
        private var myTriggerAutoPopup = false
        fun disableOnCompletionChars(ignoredChars: String): Builder {
            myIgnoredCharacters = ignoredChars
            return this
        }

        fun insertOrMove(value: String): Builder {
            myValueToInsert = value
            return this
        }

        fun triggerAutoPopup(): Builder {
            myTriggerAutoPopup = true
            return this
        }

        fun build(): DeclarativeInsertHandler {
            return DeclarativeInsertHandler(StringUtil.notNullize(myIgnoredCharacters),
                StringUtil.notNullize(myValueToInsert),
                myTriggerAutoPopup)
        }
    }
}
