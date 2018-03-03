BIN_DIR=bin
SRC_DIR=src
OBJ_DIR=obj
SRC=${wildcard ${SRC_DIR}/*.c}
OBJ=${patsubst ${SRC_DIR}/%.c, ${OBJ_DIR}/%.o, ${SRC}}

TARGET=everything
BIN_TARGET=${BIN_DIR}/${TARGET}

CC := gcc
CFLAGS := -g

${BIN_TARGET}:${OBJ}
	${CC} ${OBJ} -o $@

${OBJ_DIR}/%.o:${SRC_DIR}/%.c
	${CC} ${CFLAGS} -c $< -o $@

test:
	echo ${SRC}
	echo ${OBJ}

clean:
	find ${OBJ_DIR} -name *.o -exec rm -rf {} \;
