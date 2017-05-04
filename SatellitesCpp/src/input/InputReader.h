/*
 * InputReader.h
 *
 */

#ifndef INPUT_INPUTREADER_H_
#define INPUT_INPUTREADER_H_

#include "../model/InputData.h"

class InputReader {
public:
	explicit InputReader() {
	}
	;
	~InputReader() {
	}
	;

	InputData parseInput(const char * file_name);

};

#endif /* INPUT_INPUTREADER_H_ */
