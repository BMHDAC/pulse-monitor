package pl.pw.mierzopuls.model

data class Study(
    val id: String,
    val timeStamps: List<Long> = listOf(),
    val values: List<Int> = listOf(),
    val pulse: Int
)