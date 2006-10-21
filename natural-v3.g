grammar Natural;

options {
  //backtrack=true;
  //memoize=true;
  k=4;
}

start 	:	statement EOF ;

statement
	: prefix statement
	| call (suffix)* ('and' statement)?
	;
call returns [Apply a]
	: 'forward' 'and' 'back' { a=Apply.makeApply("forward and back"); }
	| 'dosado' { a=Apply.makeApply("dosado"); }
	| 'pass' 'thru' { a=Apply.makeApply("pass thru"); }
	| 'roll' 'away'
	| 'u' 'turn' 'back'
	| 'double' 'pass' 'thru'
	| 'half' 'sashay'
	| 'two' people 'chain'
	| 'chain' 'down' 'the' 'line'
	| 'lead' ('left'|'right')
	| 'right' 'and' 'left' 'thru'
	| 'star' 'thru'
	| 'slide' 'thru'
	| 'bend' 'the' 'line'
	| 'square' 'thru' one_four ('hands'! ('around'!|'round'!)?)?
	| 'california' 'twirl'
	| 'wheel' 'around'
	| 'box' 'the' 'gnat'
	| 'step' 'to' 'a' 'wave'
	| 'balance'
	| 'pass' 'the' 'ocean'
	| 'step' 'thru'
	| 'extend'
	| 'swing' 'thru'
	| people ('cross')? ('run'|'fold')
	| 'trade'
	| 'partner' 'trade'
	| 'couples' 'trade'
	| 'trade'
	| 'zoom'
	| 'flutter' 'wheel'
	| 'veer' ('left'|'right')
	| 'touch' ('one'|'a') 'quarter'
	| ('split'|'box') 'circulate'
	| 'turn' 'thru'
	| 'spin' 'the' 'top'
	| 'cast' 'off' 'three' 'quarters'
	| 'walk' 'and' 'dodge'
	| people 'walk' (people|'others') 'dodge'
	| 'slide' 'thru'
	| 'dixie' 'style' ('to' ('a'|'an' 'ocean') 'wave')?
	| 'tag' 'the' 'line' (('and')? 'face' ('left'|'right'|'in'|'out'))?
	| 'half' 'tag'
	| 'scoot' 'back'
	| 'single' 'hinge'
	| 'couples' 'hinge'
	| 'recycle'
	;
prefix
	: 'left' | 'reverse' | people | wave_select
	| 'as' 'couples' | 'tandem'
	;
suffix
   	: 'and' 'roll'
   	| 'and' 'sweep' ('a'|'one') 'quarter'
	| 'to' 'a' 'wave'
   	;
people
	: boys | girls | all
	;
boys
	: 'boys' | 'men' ;
girls
	: 'girls' | 'ladies' ;
all
	: 'all' | 'everyone' | 'every' 'one' | 'every' 'body' ;
wave_select
	: 'centers' | 'ends' ;
one_four
	: 'one' | 'two' | 'three' | 'four' | 'five' ;

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { channel=99; }
    ;
