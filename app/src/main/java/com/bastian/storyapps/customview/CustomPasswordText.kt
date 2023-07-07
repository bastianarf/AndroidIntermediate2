package com.bastian.storyapps.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.bastian.storyapps.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomPasswordText : TextInputEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val parentLayout = parent.parent as TextInputLayout
                if (p0.toString().length < 8) {
                    parentLayout.error = "Password tidak boleh kurang dari 8 karakter"
                } else {
                    parentLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}