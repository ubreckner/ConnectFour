package connectfour

const val FIRST_PLAYER_TILE = "o"
const val SECOND_PLAYER_TILE = "*"
const val EMPTY_TILE = " "

class Board {
    var firstPlayer: String = ""
    var firstPlayerScore = 0
    var secondPlayer: String = ""
    var secondPlayerScore = 0
    private var rows: Int = 6
    private var columns: Int = 7
    var currentGame = 1
    var gameCnt = 1
    var finished = false
    var winner = ""
    var draw = false
    private lateinit var boardState: MutableList<MutableList<String>>

    fun initBoard() {
        println("Connect Four")
        println("First player's name:")
        firstPlayer = readln()
        println("Second player's name:")
        secondPlayer = readln()
        var validInput = false
        while (!validInput) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            validInput = setRowsAndCols(readln())
        }
        validInput = false
        while (!validInput) {
            println(
                "Do you want to play single or multiple games?\n" + "For a single game, input 1 or press Enter\n" + "Input a number of games:"
            )
            validInput = readNumberOfGames()
        }
        initBoardState()
        println("$firstPlayer VS $secondPlayer")
        println("$rows X $columns board")
        println(if (this.gameCnt > 1) "Total $gameCnt games\nGame #$currentGame" else "Single game")
    }

    private fun readNumberOfGames(): Boolean {
        val input = readln()
        var valid = true
        if (input.matches("\\d".toRegex()) && input.toInt() > 0) {
            this.gameCnt = input.toInt()
        } else if (input == "") {
            //everything fine as it is
        } else {
            println("Invalid input")
            valid = false
        }
        return valid
    }

    fun initBoardState() {
        this.finished = false
        this.winner = ""
        this.draw = false
        val res = mutableListOf<MutableList<String>>()
        for (i in 0 until rows) {
            res.add(MutableList(columns) { EMPTY_TILE })
        }
        boardState = res
    }

    private fun setRowsAndCols(input: String = "6x7"): Boolean {
        var validInput = false
        val trimmedInput = input.replace("\\s".toRegex(), "").lowercase()
        if (trimmedInput.matches("\\d+x\\d+".toRegex())) {
            val (tempRow, tempCol) = trimmedInput.split("x").map { it.toInt() }
            if (tempRow !in 5..9) {
                println("Board rows should be from 5 to 9")
            } else {
                rows = tempRow
                validInput = true
            }
            if (tempCol !in 5..9) {
                println("Board columns should be from 5 to 9")
                validInput = false
            } else {
                columns = tempCol
                validInput = true && validInput
            }
        } else if (trimmedInput == "") {
            rows = 6
            columns = 7
            validInput = true
        } else {
            println("Invalid input")
        }
        return validInput
    }

    fun printBoard() {
        printFirstRow()
        printMiddleRows()
        printBottomRow()
    }

    private fun printBottomRow() {
        print("╚")
        for (i in 2..columns) {
            print("═╩")
        }
        println("═╝")
    }

    private fun printMiddleRows() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                print("║${boardState[i][j]}")
            }
            println("║")
        }
    }

    private fun printFirstRow() {
        for (i in 1..columns) {
            print(" $i")
        }
        println(" ")
    }

    fun playersTurn(playerNum: Int): Boolean {
        val tile: String
        val player: String
        var regularMove = false

        if (playerNum == 1) {
            player = firstPlayer
            tile = FIRST_PLAYER_TILE
        } else {
            player = secondPlayer
            tile = SECOND_PLAYER_TILE
        }
        println("$player's turn:")
        val playedCol = readln()
        when {
            playedCol == "end" -> {
                throw InterruptedException()
            }

            playedCol.toIntOrNull() == null -> {
                println("Incorrect column number")
            }

            playedCol.toInt() !in 1..columns -> {
                println("The column number is out of range (1 - $columns)")
            }

            else -> {
                val row = getFirstEmptyRowIdxOfColumn(playedCol.toInt())
                if (row >= rows) {
                    println("Column ${playedCol.toInt()} is full")
                } else {
                    boardState[row][playedCol.toInt() - 1] = tile
                    evaluateWinningCondition(row, playedCol.toInt() - 1, tile, player)
                    regularMove = true
                }
            }
        }
        return regularMove
    }

    private fun isHorizontalWin(row: Int, col: Int, tile: String): Boolean {
        var horizontalTileCnt = 0
        //Horizontal part
        val possibleHStart = if (col - 3 < 0) 0 else col - 3
        val possibleHEnd = if (col + 3 >= columns) columns - 1 else col + 3
        for (i in possibleHStart..possibleHEnd) {
            if (boardState[row][i] == tile) {
                horizontalTileCnt++
            } else {
                if (horizontalTileCnt < 4) {
                    horizontalTileCnt = 0
                }
            }
        }
        return horizontalTileCnt >= 4
    }

    private fun evaluateWinningCondition(row: Int, col: Int, tile: String, player: String) {
        var full = true

        if (isHorizontalWin(row, col, tile) || isVerticalWin(row, col, tile) || isDiagonalRightWin(
                row, col, tile
            ) || isDiagonalLeftWin(row, col, tile)
        ) {
            this.finished = true
            this.winner = player
            if (winner == firstPlayer) firstPlayerScore += 2 else secondPlayerScore += 2
            currentGame++
        }

        //Board Full
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (boardState[i][j] == EMPTY_TILE) {
                    full = false
                }
            }
        }
        if (full) {
            this.finished = true
            this.draw = true
            this.firstPlayerScore++
            this.secondPlayerScore++
            currentGame++
        }
    }

    private fun isDiagonalLeftWin(row: Int, col: Int, tile: String): Boolean {
        var diagonalLeftTileCnt = 0

        //Hard Part Diagonal /
        var i = row
        var j = col
        while (i >= 0 && j < columns) {
            if (boardState[i--][j++] == tile) {
                diagonalLeftTileCnt++
            } else {
                break
            }
        }
        i = row + 1
        j = col - 1
        while (i < rows && j >= 0) {
            if (boardState[i++][j--] == tile) {
                diagonalLeftTileCnt++
            } else {
                break
            }
        }
        return diagonalLeftTileCnt >= 4
    }

    private fun isDiagonalRightWin(row: Int, col: Int, tile: String): Boolean {
        var diagonalRightTileCnt = 0

        //Hard Part Diagonal \
        var i = row
        var j = col
        while (i >= 0 && j >= 0) {
            if (boardState[i--][j--] == tile) {
                diagonalRightTileCnt++
            } else {
                break
            }
        }
        i = row + 1
        j = col + 1
        while (i < rows && j < columns) {
            if (boardState[i++][j++] == tile) {
                diagonalRightTileCnt++
            } else {
                break
            }
        }
        return diagonalRightTileCnt >= 4
    }

    private fun isVerticalWin(row: Int, col: Int, tile: String): Boolean {
        var verticalTileCnt = 0
        //Vertical part
        val possibleVStart = if (row - 3 < 0) 0 else row - 3
        val possibleVEnd = if (row + 3 >= rows) rows - 1 else row + 3
        for (i in possibleVStart..possibleVEnd) {
            if (boardState[i][col] == tile) {
                verticalTileCnt++
            } else {
                if (verticalTileCnt < 4) {
                    verticalTileCnt = 0
                }
            }
        }
        return verticalTileCnt >= 4
    }

    private fun getFirstEmptyRowIdxOfColumn(column: Int): Int {
        var res = rows
        for (i in rows - 1 downTo 0) {
            if (boardState[i][column - 1] == EMPTY_TILE) {
                res = i
                break
            }
        }
        return res
    }
}

fun main() {
    val board = Board()
    board.initBoard()
    board.printBoard()
    var nextPlayer = 1

    try {
        while (board.currentGame <= board.gameCnt) {
            while (!board.finished) {
                if (board.playersTurn(nextPlayer % 2)) {
                    nextPlayer++
                    board.printBoard()
                }
            }
            if (board.winner != "") println("Player ${board.winner} won")
            if (board.draw) println("It is a draw")

            if (board.gameCnt > 1) {
                println("Score")
                println("${board.firstPlayer}: ${board.firstPlayerScore} ${board.secondPlayer}: ${board.secondPlayerScore}")
                if (board.currentGame <= board.gameCnt) {
                    println("Game #${board.currentGame}")
                    board.initBoardState()
                    board.printBoard()
                    nextPlayer = board.currentGame % 2
                }
            }
        }
    } catch (e: InterruptedException) {
        //Just skip to the end
    }
    println("Game over!")
}