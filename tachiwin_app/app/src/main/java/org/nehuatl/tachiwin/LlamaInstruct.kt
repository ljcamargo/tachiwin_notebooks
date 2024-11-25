package org.nehuatl.tachiwin

object LlamaInstruct {
    const val SYS = "Below is an instruction that describes a task, paired with an input that provides further context. Write a response that appropriately completes the request."

    fun prompt(instruction: String, input: String): String {
        return  "$SYS\n\n### Instruction:\n$instruction\n\n### Input:\n$input\n\n### Response:\n"
    }

    fun untokenedText(text: String): String = text
        .replace("<|start_header_id|>assistant<|end_header_id|>","")
        .replace("<|start_header_id|>user<|end_header_id|>","")
        .replace("<|start_header_id|>system<|end_header_id|>","")
        .replace("<|eot_id|>","")
        .replace("<|start_text|>","")
        .replace("<|end_of_text|>","")
        .replace("<|eot_text|>","")
        .replace("<|","")
        .replace("|>","")
        .replace("<","")
        .replace(">","")
        .replace("|","")
        .replace("eot_id","")
        .replace("eot_text","")
        .replace("end_of_text","")
}