package net.cscott.sdr.calls.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import net.cscott.sdr.calls.grm.Grm;
import net.cscott.sdr.util.Tools;

/** Post-processed grammar for all dance programs. */
public class AllGrm /*extends GrmDB*/ {
    public static final Map<String,Grm> BASIC;
    public static final Map<String,Grm> MAINSTREAM;
    public static final Map<String,Grm> PLUS;
    public static final Map<String,Grm> A1;
    public static final Map<String,Grm> A2;
    public static final Map<String,Grm> C1;
    public static final Map<String,Grm> C2;
    public static final Map<String,Grm> C3A;
    public static final Map<String,Grm> C3B;
    public static final Map<String,Grm> C4;
    static {
        List<Grm> l = new ArrayList<Grm>();
        Map<String,Grm> _BASIC = new HashMap<String,Grm>();
        l.add(new Grm.Nonterminal("anything_0",null,0)); // 0
        _BASIC.put("anything",l.get(0));
        l.add(new Grm.Terminal("do")); // 1
        l.add(new Grm.Terminal("half")); // 2
        l.add(new Grm.Terminal("of")); // 3
        l.add(new Grm.Terminal("a")); // 4
        l.add(new Grm.Mult(l.get(4),Grm.Mult.Type.QUESTION)); // 5
        l.add(new Grm.Nonterminal("anything_1","anything",0)); // 6
        l.add(new Grm.Nonterminal("anything_0_suffix",null,-1)); // 7
        l.add(new Grm.Mult(l.get(7),Grm.Mult.Type.STAR)); // 8
        l.add(new Grm.Concat(Tools.l(l.get(2),l.get(3),l.get(5),l.get(6),l.get(8)))); // 9
        l.add(new Grm.Nonterminal("fraction",0)); // 10
        l.add(new Grm.Concat(Tools.l(l.get(3),l.get(5)))); // 11
        l.add(new Grm.Mult(l.get(11),Grm.Mult.Type.QUESTION)); // 12
        l.add(new Grm.Nonterminal("anything_1","anything",1)); // 13
        l.add(new Grm.Concat(Tools.l(l.get(10),l.get(12),l.get(13),l.get(8)))); // 14
        l.add(new Grm.Alt(Tools.l(l.get(9),l.get(14)))); // 15
        l.add(new Grm.Concat(Tools.l(l.get(1),l.get(15)))); // 16
        l.add(new Grm.Concat(Tools.l(l.get(6),l.get(8)))); // 17
        l.add(new Grm.Alt(Tools.l(l.get(16),l.get(17)))); // 18
        _BASIC.put("anything_0",l.get(18));
        l.add(new Grm.Nonterminal("cardinal",0)); // 19
        _BASIC.put("anything_0_suffix",l.get(19));
        l.add(new Grm.Nonterminal("anything_2",null,0)); // 20
        l.add(new Grm.Nonterminal("anyone",0)); // 21
        l.add(new Grm.Terminal("run")); // 22
        l.add(new Grm.Terminal("fold")); // 23
        l.add(new Grm.Terminal("cross")); // 24
        l.add(new Grm.Alt(Tools.l(l.get(22),l.get(23)))); // 25
        l.add(new Grm.Concat(Tools.l(l.get(24),l.get(25)))); // 26
        l.add(new Grm.Terminal("walk")); // 27
        l.add(new Grm.Terminal("others")); // 28
        l.add(new Grm.Nonterminal("anyone",-1)); // 29
        l.add(new Grm.Alt(Tools.l(l.get(28),l.get(29)))); // 30
        l.add(new Grm.Terminal("dodge")); // 31
        l.add(new Grm.Concat(Tools.l(l.get(27),l.get(30),l.get(31)))); // 32
        l.add(new Grm.Terminal("u")); // 33
        l.add(new Grm.Terminal("turn")); // 34
        l.add(new Grm.Terminal("back")); // 35
        l.add(new Grm.Concat(Tools.l(l.get(33),l.get(34),l.get(35)))); // 36
        l.add(new Grm.Alt(Tools.l(l.get(22),l.get(23),l.get(26),l.get(32),l.get(36)))); // 37
        l.add(new Grm.Concat(Tools.l(l.get(21),l.get(37)))); // 38
        l.add(new Grm.Terminal("touch")); // 39
        l.add(new Grm.Concat(Tools.l(l.get(39),l.get(10)))); // 40
        l.add(new Grm.Terminal("two")); // 41
        l.add(new Grm.Nonterminal("genders",0)); // 42
        l.add(new Grm.Terminal("chain")); // 43
        l.add(new Grm.Concat(Tools.l(l.get(41),l.get(42),l.get(43)))); // 44
        l.add(new Grm.Terminal("courtesy")); // 45
        l.add(new Grm.Concat(Tools.l(l.get(45),l.get(34),l.get(10)))); // 46
        l.add(new Grm.Terminal("circle")); // 47
        l.add(new Grm.Terminal("left")); // 48
        l.add(new Grm.Concat(Tools.l(l.get(48),l.get(10)))); // 49
        l.add(new Grm.Terminal("right")); // 50
        l.add(new Grm.Concat(Tools.l(l.get(50),l.get(10)))); // 51
        l.add(new Grm.Alt(Tools.l(l.get(49),l.get(51)))); // 52
        l.add(new Grm.Concat(Tools.l(l.get(47),l.get(52)))); // 53
        l.add(new Grm.Alt(Tools.l(l.get(20),l.get(38),l.get(40),l.get(44),l.get(46),l.get(53)))); // 54
        _BASIC.put("anything_1",l.get(54));
        l.add(new Grm.Nonterminal("anything_3",null,0)); // 55
        l.add(new Grm.Nonterminal("leftable_anything",0)); // 56
        l.add(new Grm.Concat(Tools.l(l.get(48),l.get(56)))); // 57
        l.add(new Grm.Terminal("reverse")); // 58
        l.add(new Grm.Nonterminal("reversable_anything",0)); // 59
        l.add(new Grm.Concat(Tools.l(l.get(58),l.get(59)))); // 60
        l.add(new Grm.Alt(Tools.l(l.get(55),l.get(57),l.get(60)))); // 61
        _BASIC.put("anything_2",l.get(61));
        l.add(new Grm.Terminal("zoom")); // 62
        l.add(new Grm.Terminal("recycle")); // 63
        l.add(new Grm.Terminal("nothing")); // 64
        l.add(new Grm.Terminal("hinge")); // 65
        l.add(new Grm.Terminal("balance")); // 66
        l.add(new Grm.Terminal("trade")); // 67
        l.add(new Grm.Terminal("all")); // 68
        l.add(new Grm.Terminal("eight")); // 69
        l.add(new Grm.Concat(Tools.l(l.get(68),l.get(69)))); // 70
        l.add(new Grm.Mult(l.get(70),Grm.Mult.Type.QUESTION)); // 71
        l.add(new Grm.Terminal("circulate")); // 72
        l.add(new Grm.Concat(Tools.l(l.get(71),l.get(72)))); // 73
        l.add(new Grm.Nonterminal("parenthesized_anything",null,0)); // 74
        l.add(new Grm.Terminal("bend")); // 75
        l.add(new Grm.Terminal("the")); // 76
        l.add(new Grm.Terminal("line")); // 77
        l.add(new Grm.Concat(Tools.l(l.get(75),l.get(76),l.get(77)))); // 78
        l.add(new Grm.Terminal("pass")); // 79
        l.add(new Grm.Terminal("ocean")); // 80
        l.add(new Grm.Concat(Tools.l(l.get(79),l.get(76),l.get(80)))); // 81
        l.add(new Grm.Terminal("and")); // 82
        l.add(new Grm.Concat(Tools.l(l.get(27),l.get(82),l.get(31)))); // 83
        l.add(new Grm.Terminal("lead")); // 84
        l.add(new Grm.Alt(Tools.l(l.get(50),l.get(48)))); // 85
        l.add(new Grm.Concat(Tools.l(l.get(84),l.get(85)))); // 86
        l.add(new Grm.Terminal("face")); // 87
        l.add(new Grm.Concat(Tools.l(l.get(87),l.get(85)))); // 88
        l.add(new Grm.Terminal("forward")); // 89
        l.add(new Grm.Concat(Tools.l(l.get(89),l.get(82),l.get(35)))); // 90
        l.add(new Grm.Terminal("slide")); // 91
        l.add(new Grm.Terminal("thru")); // 92
        l.add(new Grm.Concat(Tools.l(l.get(91),l.get(92)))); // 93
        l.add(new Grm.Terminal("veer")); // 94
        l.add(new Grm.Alt(Tools.l(l.get(48),l.get(50)))); // 95
        l.add(new Grm.Concat(Tools.l(l.get(94),l.get(95)))); // 96
        l.add(new Grm.Concat(Tools.l(l.get(50),l.get(82),l.get(48),l.get(92)))); // 97
        l.add(new Grm.Terminal("column")); // 98
        l.add(new Grm.Concat(Tools.l(l.get(98),l.get(72)))); // 99
        l.add(new Grm.Terminal("star")); // 100
        l.add(new Grm.Concat(Tools.l(l.get(100),l.get(92)))); // 101
        l.add(new Grm.Terminal("box")); // 102
        l.add(new Grm.Terminal("gnat")); // 103
        l.add(new Grm.Concat(Tools.l(l.get(76),l.get(103)))); // 104
        l.add(new Grm.Alt(Tools.l(l.get(72),l.get(104)))); // 105
        l.add(new Grm.Concat(Tools.l(l.get(102),l.get(105)))); // 106
        l.add(new Grm.Terminal("down")); // 107
        l.add(new Grm.Concat(Tools.l(l.get(43),l.get(107),l.get(76),l.get(77)))); // 108
        l.add(new Grm.Terminal("step")); // 109
        l.add(new Grm.Terminal("to")); // 110
        l.add(new Grm.Terminal("wave")); // 111
        l.add(new Grm.Concat(Tools.l(l.get(110),l.get(4),l.get(111)))); // 112
        l.add(new Grm.Alt(Tools.l(l.get(92),l.get(112)))); // 113
        l.add(new Grm.Concat(Tools.l(l.get(109),l.get(113)))); // 114
        l.add(new Grm.Terminal("couples")); // 115
        l.add(new Grm.Concat(Tools.l(l.get(48),l.get(65)))); // 116
        l.add(new Grm.Alt(Tools.l(l.get(67),l.get(72),l.get(65),l.get(116)))); // 117
        l.add(new Grm.Concat(Tools.l(l.get(115),l.get(117)))); // 118
        l.add(new Grm.Terminal("sides")); // 119
        l.add(new Grm.Terminal("start")); // 120
        l.add(new Grm.Concat(Tools.l(l.get(119),l.get(120)))); // 121
        l.add(new Grm.Terminal("dixie")); // 122
        l.add(new Grm.Terminal("style")); // 123
        l.add(new Grm.Mult(l.get(112),Grm.Mult.Type.QUESTION)); // 124
        l.add(new Grm.Concat(Tools.l(l.get(122),l.get(123),l.get(124)))); // 125
        l.add(new Grm.Terminal("heads")); // 126
        l.add(new Grm.Concat(Tools.l(l.get(126),l.get(120)))); // 127
        l.add(new Grm.Terminal("double")); // 128
        l.add(new Grm.Concat(Tools.l(l.get(128),l.get(79),l.get(92)))); // 129
        l.add(new Grm.Terminal("california")); // 130
        l.add(new Grm.Terminal("twirl")); // 131
        l.add(new Grm.Concat(Tools.l(l.get(130),l.get(131)))); // 132
        l.add(new Grm.Concat(Tools.l(l.get(48),l.get(82),l.get(50),l.get(92)))); // 133
        l.add(new Grm.Alt(Tools.l(l.get(62),l.get(63),l.get(64),l.get(65),l.get(66),l.get(67),l.get(73),l.get(56),l.get(59),l.get(74),l.get(78),l.get(81),l.get(83),l.get(86),l.get(88),l.get(90),l.get(93),l.get(96),l.get(97),l.get(99),l.get(101),l.get(106),l.get(108),l.get(114),l.get(118),l.get(121),l.get(125),l.get(127),l.get(129),l.get(132),l.get(133),l.get(36)))); // 134
        _BASIC.put("anything_3",l.get(134));
        l.add(new Grm.Terminal("dosado")); // 135
        l.add(new Grm.Terminal("extend")); // 136
        l.add(new Grm.Concat(Tools.l(l.get(34),l.get(92)))); // 137
        l.add(new Grm.Terminal("scoot")); // 138
        l.add(new Grm.Concat(Tools.l(l.get(138),l.get(35)))); // 139
        l.add(new Grm.Terminal("square")); // 140
        l.add(new Grm.Nonterminal("number",0)); // 141
        l.add(new Grm.Terminal("hands")); // 142
        l.add(new Grm.Terminal("around")); // 143
        l.add(new Grm.Terminal("round")); // 144
        l.add(new Grm.Alt(Tools.l(l.get(143),l.get(144)))); // 145
        l.add(new Grm.Mult(l.get(145),Grm.Mult.Type.QUESTION)); // 146
        l.add(new Grm.Concat(Tools.l(l.get(142),l.get(146)))); // 147
        l.add(new Grm.Mult(l.get(147),Grm.Mult.Type.QUESTION)); // 148
        l.add(new Grm.Concat(Tools.l(l.get(92),l.get(141),l.get(148)))); // 149
        l.add(new Grm.Alt(Tools.l(l.get(92),l.get(149)))); // 150
        l.add(new Grm.Concat(Tools.l(l.get(140),l.get(150)))); // 151
        l.add(new Grm.Terminal("swing")); // 152
        l.add(new Grm.Concat(Tools.l(l.get(152),l.get(92)))); // 153
        l.add(new Grm.Terminal("tag")); // 154
        l.add(new Grm.Terminal("way")); // 155
        l.add(new Grm.Terminal("through")); // 156
        l.add(new Grm.Mult(l.get(156),Grm.Mult.Type.QUESTION)); // 157
        l.add(new Grm.Concat(Tools.l(l.get(68),l.get(76),l.get(155),l.get(157)))); // 158
        l.add(new Grm.Mult(l.get(158),Grm.Mult.Type.QUESTION)); // 159
        l.add(new Grm.Alt(Tools.l(l.get(159),l.get(10)))); // 160
        l.add(new Grm.Concat(Tools.l(l.get(77),l.get(160)))); // 161
        l.add(new Grm.Alt(Tools.l(l.get(161)))); // 162
        l.add(new Grm.Concat(Tools.l(l.get(76),l.get(162)))); // 163
        l.add(new Grm.Alt(Tools.l(l.get(163)))); // 164
        l.add(new Grm.Concat(Tools.l(l.get(154),l.get(164)))); // 165
        l.add(new Grm.Terminal("spin")); // 166
        l.add(new Grm.Terminal("top")); // 167
        l.add(new Grm.Concat(Tools.l(l.get(166),l.get(76),l.get(167)))); // 168
        l.add(new Grm.Concat(Tools.l(l.get(79),l.get(92)))); // 169
        l.add(new Grm.Terminal("pull")); // 170
        l.add(new Grm.Terminal("by")); // 171
        l.add(new Grm.Concat(Tools.l(l.get(170),l.get(171)))); // 172
        l.add(new Grm.Alt(Tools.l(l.get(39),l.get(135),l.get(136),l.get(137),l.get(139),l.get(151),l.get(153),l.get(165),l.get(168),l.get(169),l.get(172)))); // 173
        _BASIC.put("leftable_anything",l.get(173));
        l.add(new Grm.Terminal("flutter")); // 174
        l.add(new Grm.Terminal("wheel")); // 175
        l.add(new Grm.Concat(Tools.l(l.get(174),l.get(175)))); // 176
        l.add(new Grm.Terminal("sashay")); // 177
        l.add(new Grm.Concat(Tools.l(l.get(2),l.get(177)))); // 178
        l.add(new Grm.Terminal("roll")); // 179
        l.add(new Grm.Terminal("away")); // 180
        l.add(new Grm.Concat(Tools.l(l.get(21),l.get(179),l.get(180)))); // 181
        l.add(new Grm.Concat(Tools.l(l.get(179),l.get(180)))); // 182
        l.add(new Grm.Concat(Tools.l(l.get(175),l.get(143)))); // 183
        l.add(new Grm.Alt(Tools.l(l.get(176),l.get(178),l.get(181),l.get(182),l.get(183)))); // 184
        _BASIC.put("reversable_anything",l.get(184));
        l.add(new Grm.Terminal("\050")); // 185
        l.add(new Grm.Nonterminal("anything",-1)); // 186
        l.add(new Grm.Terminal("\051")); // 187
        l.add(new Grm.Concat(Tools.l(l.get(185),l.get(186),l.get(187)))); // 188
        _BASIC.put("parenthesized_anything",l.get(188));
        l.add(new Grm.Nonterminal("genders",-1)); // 189
        l.add(new Grm.Nonterminal("all",-1)); // 190
        l.add(new Grm.Alt(Tools.l(l.get(189),l.get(190)))); // 191
        _BASIC.put("people",l.get(191));
        l.add(new Grm.Nonterminal("boys",-1)); // 192
        l.add(new Grm.Nonterminal("girls",-1)); // 193
        l.add(new Grm.Alt(Tools.l(l.get(192),l.get(193)))); // 194
        _BASIC.put("genders",l.get(194));
        l.add(new Grm.Terminal("boys")); // 195
        l.add(new Grm.Terminal("men")); // 196
        l.add(new Grm.Alt(Tools.l(l.get(195),l.get(196)))); // 197
        _BASIC.put("boys",l.get(197));
        l.add(new Grm.Terminal("girls")); // 198
        l.add(new Grm.Terminal("ladies")); // 199
        l.add(new Grm.Alt(Tools.l(l.get(198),l.get(199)))); // 200
        _BASIC.put("girls",l.get(200));
        l.add(new Grm.Terminal("everyone")); // 201
        l.add(new Grm.Terminal("every")); // 202
        l.add(new Grm.Terminal("one")); // 203
        l.add(new Grm.Terminal("body")); // 204
        l.add(new Grm.Alt(Tools.l(l.get(203),l.get(204)))); // 205
        l.add(new Grm.Concat(Tools.l(l.get(202),l.get(205)))); // 206
        l.add(new Grm.Alt(Tools.l(l.get(68),l.get(201),l.get(206)))); // 207
        _BASIC.put("all",l.get(207));
        l.add(new Grm.Terminal("centers")); // 208
        l.add(new Grm.Terminal("ends")); // 209
        l.add(new Grm.Alt(Tools.l(l.get(208),l.get(209)))); // 210
        _BASIC.put("wave_select",l.get(210));
        l.add(new Grm.Nonterminal("people",-1)); // 211
        l.add(new Grm.Nonterminal("wave_select",-1)); // 212
        l.add(new Grm.Alt(Tools.l(l.get(211),l.get(212)))); // 213
        _BASIC.put("anyone",l.get(213));
        l.add(new Grm.Nonterminal("digit",-1)); // 214
        l.add(new Grm.Nonterminal("fraction",-1)); // 215
        l.add(new Grm.Nonterminal("NUMBER",-1)); // 216
        l.add(new Grm.Concat(Tools.l(l.get(214),l.get(82),l.get(215)))); // 217
        l.add(new Grm.Alt(Tools.l(l.get(214),l.get(215),l.get(216),l.get(217)))); // 218
        _BASIC.put("number",l.get(218));
        l.add(new Grm.Nonterminal("digit_greater_than_two",-1)); // 219
        l.add(new Grm.Alt(Tools.l(l.get(203),l.get(41),l.get(219)))); // 220
        _BASIC.put("digit",l.get(220));
        l.add(new Grm.Terminal("three")); // 221
        l.add(new Grm.Terminal("four")); // 222
        l.add(new Grm.Terminal("five")); // 223
        l.add(new Grm.Terminal("six")); // 224
        l.add(new Grm.Terminal("seven")); // 225
        l.add(new Grm.Terminal("nine")); // 226
        l.add(new Grm.Alt(Tools.l(l.get(221),l.get(222),l.get(223),l.get(224),l.get(225),l.get(69),l.get(226)))); // 227
        _BASIC.put("digit_greater_than_two",l.get(227));
        l.add(new Grm.Alt(Tools.l(l.get(4),l.get(203)))); // 228
        l.add(new Grm.Terminal("third")); // 229
        l.add(new Grm.Terminal("quarter")); // 230
        l.add(new Grm.Alt(Tools.l(l.get(2),l.get(229),l.get(230)))); // 231
        l.add(new Grm.Concat(Tools.l(l.get(228),l.get(231)))); // 232
        l.add(new Grm.Terminal("thirds")); // 233
        l.add(new Grm.Terminal("quarters")); // 234
        l.add(new Grm.Alt(Tools.l(l.get(233),l.get(234)))); // 235
        l.add(new Grm.Concat(Tools.l(l.get(41),l.get(235)))); // 236
        l.add(new Grm.Concat(Tools.l(l.get(221),l.get(234)))); // 237
        l.add(new Grm.Alt(Tools.l(l.get(232),l.get(236),l.get(237)))); // 238
        _BASIC.put("fraction",l.get(238));
        l.add(new Grm.Terminal("once")); // 239
        l.add(new Grm.Concat(Tools.l(l.get(239),l.get(82),l.get(215)))); // 240
        l.add(new Grm.Terminal("twice")); // 241
        l.add(new Grm.Concat(Tools.l(l.get(82),l.get(215)))); // 242
        l.add(new Grm.Mult(l.get(242),Grm.Mult.Type.QUESTION)); // 243
        l.add(new Grm.Concat(Tools.l(l.get(241),l.get(243)))); // 244
        l.add(new Grm.Terminal("times")); // 245
        l.add(new Grm.Concat(Tools.l(l.get(219),l.get(243),l.get(245)))); // 246
        l.add(new Grm.Concat(Tools.l(l.get(216),l.get(245)))); // 247
        l.add(new Grm.Alt(Tools.l(l.get(240),l.get(244),l.get(246),l.get(247)))); // 248
        _BASIC.put("cardinal",l.get(248));
        l.add(new Grm.Nonterminal("EOF",-1)); // 249
        l.add(new Grm.Concat(Tools.l(l.get(186),l.get(249)))); // 250
        _BASIC.put("start",l.get(250));
        BASIC = Collections.unmodifiableMap(_BASIC);
        Map<String,Grm> _MAINSTREAM = new HashMap<String,Grm>();
        _MAINSTREAM.put("anything",l.get(0));
        _MAINSTREAM.put("anything_0",l.get(18));
        _MAINSTREAM.put("anything_0_suffix",l.get(19));
        l.add(new Grm.Terminal("cast")); // 251
        l.add(new Grm.Terminal("off")); // 252
        l.add(new Grm.Mult(l.get(252),Grm.Mult.Type.QUESTION)); // 253
        l.add(new Grm.Concat(Tools.l(l.get(251),l.get(253),l.get(10)))); // 254
        l.add(new Grm.Alt(Tools.l(l.get(20),l.get(38),l.get(40),l.get(44),l.get(46),l.get(254),l.get(53)))); // 255
        _MAINSTREAM.put("anything_1",l.get(255));
        _MAINSTREAM.put("anything_2",l.get(61));
        l.add(new Grm.Terminal("ferris")); // 256
        l.add(new Grm.Concat(Tools.l(l.get(256),l.get(175)))); // 257
        l.add(new Grm.Alt(Tools.l(l.get(62),l.get(63),l.get(64),l.get(65),l.get(66),l.get(67),l.get(73),l.get(56),l.get(59),l.get(74),l.get(78),l.get(81),l.get(83),l.get(86),l.get(88),l.get(90),l.get(93),l.get(96),l.get(97),l.get(99),l.get(101),l.get(106),l.get(108),l.get(114),l.get(118),l.get(121),l.get(257),l.get(125),l.get(127),l.get(129),l.get(132),l.get(133),l.get(36)))); // 258
        _MAINSTREAM.put("anything_3",l.get(258));
        _MAINSTREAM.put("leftable_anything",l.get(173));
        _MAINSTREAM.put("reversable_anything",l.get(184));
        _MAINSTREAM.put("parenthesized_anything",l.get(188));
        _MAINSTREAM.put("people",l.get(191));
        _MAINSTREAM.put("genders",l.get(194));
        _MAINSTREAM.put("boys",l.get(197));
        _MAINSTREAM.put("girls",l.get(200));
        _MAINSTREAM.put("all",l.get(207));
        _MAINSTREAM.put("wave_select",l.get(210));
        _MAINSTREAM.put("anyone",l.get(213));
        _MAINSTREAM.put("number",l.get(218));
        _MAINSTREAM.put("digit",l.get(220));
        _MAINSTREAM.put("digit_greater_than_two",l.get(227));
        _MAINSTREAM.put("fraction",l.get(238));
        _MAINSTREAM.put("cardinal",l.get(248));
        _MAINSTREAM.put("start",l.get(250));
        MAINSTREAM = Collections.unmodifiableMap(_MAINSTREAM);
        Map<String,Grm> _PLUS = new HashMap<String,Grm>();
        _PLUS.put("anything",l.get(0));
        _PLUS.put("anything_0",l.get(18));
        l.add(new Grm.Concat(Tools.l(l.get(82),l.get(179)))); // 259
        l.add(new Grm.Alt(Tools.l(l.get(19),l.get(259)))); // 260
        _PLUS.put("anything_0_suffix",l.get(260));
        l.add(new Grm.Terminal("track")); // 261
        l.add(new Grm.Concat(Tools.l(l.get(261),l.get(141)))); // 262
        l.add(new Grm.Alt(Tools.l(l.get(20),l.get(38),l.get(40),l.get(44),l.get(46),l.get(254),l.get(53),l.get(262)))); // 263
        _PLUS.put("anything_1",l.get(263));
        _PLUS.put("anything_2",l.get(61));
        l.add(new Grm.Alt(Tools.l(l.get(62),l.get(179),l.get(63),l.get(64),l.get(65),l.get(66),l.get(67),l.get(73),l.get(56),l.get(59),l.get(74),l.get(78),l.get(81),l.get(83),l.get(86),l.get(88),l.get(90),l.get(93),l.get(96),l.get(97),l.get(99),l.get(101),l.get(106),l.get(108),l.get(114),l.get(118),l.get(121),l.get(257),l.get(125),l.get(127),l.get(129),l.get(132),l.get(133),l.get(36)))); // 264
        _PLUS.put("anything_3",l.get(264));
        l.add(new Grm.Terminal("fan")); // 265
        l.add(new Grm.Concat(Tools.l(l.get(265),l.get(76),l.get(167)))); // 266
        l.add(new Grm.Alt(Tools.l(l.get(39),l.get(135),l.get(136),l.get(137),l.get(139),l.get(151),l.get(153),l.get(165),l.get(168),l.get(169),l.get(266),l.get(172)))); // 267
        _PLUS.put("leftable_anything",l.get(267));
        _PLUS.put("reversable_anything",l.get(184));
        _PLUS.put("parenthesized_anything",l.get(188));
        _PLUS.put("people",l.get(191));
        _PLUS.put("genders",l.get(194));
        _PLUS.put("boys",l.get(197));
        _PLUS.put("girls",l.get(200));
        _PLUS.put("all",l.get(207));
        _PLUS.put("wave_select",l.get(210));
        _PLUS.put("anyone",l.get(213));
        _PLUS.put("number",l.get(218));
        _PLUS.put("digit",l.get(220));
        _PLUS.put("digit_greater_than_two",l.get(227));
        _PLUS.put("fraction",l.get(238));
        _PLUS.put("cardinal",l.get(248));
        _PLUS.put("start",l.get(250));
        PLUS = Collections.unmodifiableMap(_PLUS);
        Map<String,Grm> _A1 = new HashMap<String,Grm>();
        _A1.put("anything",l.get(0));
        _A1.put("anything_0",l.get(18));
        _A1.put("anything_0_suffix",l.get(260));
        _A1.put("anything_1",l.get(263));
        _A1.put("anything_2",l.get(61));
        l.add(new Grm.Terminal("out")); // 268
        l.add(new Grm.Terminal("in")); // 269
        l.add(new Grm.Alt(Tools.l(l.get(268),l.get(269),l.get(50),l.get(48)))); // 270
        l.add(new Grm.Concat(Tools.l(l.get(230),l.get(270)))); // 271
        l.add(new Grm.Alt(Tools.l(l.get(62),l.get(179),l.get(63),l.get(64),l.get(65),l.get(66),l.get(67),l.get(73),l.get(56),l.get(59),l.get(74),l.get(78),l.get(81),l.get(83),l.get(86),l.get(271),l.get(88),l.get(90),l.get(93),l.get(96),l.get(97),l.get(99),l.get(101),l.get(106),l.get(108),l.get(114),l.get(118),l.get(121),l.get(257),l.get(125),l.get(127),l.get(129),l.get(132),l.get(133),l.get(36)))); // 272
        _A1.put("anything_3",l.get(272));
        _A1.put("leftable_anything",l.get(267));
        _A1.put("reversable_anything",l.get(184));
        _A1.put("parenthesized_anything",l.get(188));
        _A1.put("people",l.get(191));
        _A1.put("genders",l.get(194));
        _A1.put("boys",l.get(197));
        _A1.put("girls",l.get(200));
        _A1.put("all",l.get(207));
        _A1.put("wave_select",l.get(210));
        _A1.put("anyone",l.get(213));
        _A1.put("number",l.get(218));
        _A1.put("digit",l.get(220));
        _A1.put("digit_greater_than_two",l.get(227));
        _A1.put("fraction",l.get(238));
        _A1.put("cardinal",l.get(248));
        _A1.put("start",l.get(250));
        A1 = Collections.unmodifiableMap(_A1);
        Map<String,Grm> _A2 = new HashMap<String,Grm>();
        _A2.put("anything",l.get(0));
        _A2.put("anything_0",l.get(18));
        _A2.put("anything_0_suffix",l.get(260));
        _A2.put("anything_1",l.get(263));
        _A2.put("anything_2",l.get(61));
        _A2.put("anything_3",l.get(272));
        _A2.put("leftable_anything",l.get(267));
        _A2.put("reversable_anything",l.get(184));
        _A2.put("parenthesized_anything",l.get(188));
        _A2.put("people",l.get(191));
        _A2.put("genders",l.get(194));
        _A2.put("boys",l.get(197));
        _A2.put("girls",l.get(200));
        _A2.put("all",l.get(207));
        _A2.put("wave_select",l.get(210));
        _A2.put("anyone",l.get(213));
        _A2.put("number",l.get(218));
        _A2.put("digit",l.get(220));
        _A2.put("digit_greater_than_two",l.get(227));
        _A2.put("fraction",l.get(238));
        _A2.put("cardinal",l.get(248));
        _A2.put("start",l.get(250));
        A2 = Collections.unmodifiableMap(_A2);
        Map<String,Grm> _C1 = new HashMap<String,Grm>();
        _C1.put("anything",l.get(0));
        _C1.put("anything_0",l.get(18));
        _C1.put("anything_0_suffix",l.get(260));
        _C1.put("anything_1",l.get(263));
        _C1.put("anything_2",l.get(61));
        _C1.put("anything_3",l.get(272));
        _C1.put("leftable_anything",l.get(267));
        _C1.put("reversable_anything",l.get(184));
        _C1.put("parenthesized_anything",l.get(188));
        _C1.put("people",l.get(191));
        _C1.put("genders",l.get(194));
        _C1.put("boys",l.get(197));
        _C1.put("girls",l.get(200));
        _C1.put("all",l.get(207));
        _C1.put("wave_select",l.get(210));
        _C1.put("anyone",l.get(213));
        _C1.put("number",l.get(218));
        _C1.put("digit",l.get(220));
        _C1.put("digit_greater_than_two",l.get(227));
        _C1.put("fraction",l.get(238));
        _C1.put("cardinal",l.get(248));
        _C1.put("start",l.get(250));
        C1 = Collections.unmodifiableMap(_C1);
        Map<String,Grm> _C2 = new HashMap<String,Grm>();
        _C2.put("anything",l.get(0));
        _C2.put("anything_0",l.get(18));
        _C2.put("anything_0_suffix",l.get(260));
        _C2.put("anything_1",l.get(263));
        _C2.put("anything_2",l.get(61));
        _C2.put("anything_3",l.get(272));
        _C2.put("leftable_anything",l.get(267));
        _C2.put("reversable_anything",l.get(184));
        _C2.put("parenthesized_anything",l.get(188));
        _C2.put("people",l.get(191));
        _C2.put("genders",l.get(194));
        _C2.put("boys",l.get(197));
        _C2.put("girls",l.get(200));
        _C2.put("all",l.get(207));
        _C2.put("wave_select",l.get(210));
        _C2.put("anyone",l.get(213));
        _C2.put("number",l.get(218));
        _C2.put("digit",l.get(220));
        _C2.put("digit_greater_than_two",l.get(227));
        _C2.put("fraction",l.get(238));
        _C2.put("cardinal",l.get(248));
        _C2.put("start",l.get(250));
        C2 = Collections.unmodifiableMap(_C2);
        Map<String,Grm> _C3A = new HashMap<String,Grm>();
        _C3A.put("anything",l.get(0));
        _C3A.put("anything_0",l.get(18));
        _C3A.put("anything_0_suffix",l.get(260));
        _C3A.put("anything_1",l.get(263));
        _C3A.put("anything_2",l.get(61));
        _C3A.put("anything_3",l.get(272));
        _C3A.put("leftable_anything",l.get(267));
        _C3A.put("reversable_anything",l.get(184));
        _C3A.put("parenthesized_anything",l.get(188));
        _C3A.put("people",l.get(191));
        _C3A.put("genders",l.get(194));
        _C3A.put("boys",l.get(197));
        _C3A.put("girls",l.get(200));
        _C3A.put("all",l.get(207));
        _C3A.put("wave_select",l.get(210));
        _C3A.put("anyone",l.get(213));
        _C3A.put("number",l.get(218));
        _C3A.put("digit",l.get(220));
        _C3A.put("digit_greater_than_two",l.get(227));
        _C3A.put("fraction",l.get(238));
        _C3A.put("cardinal",l.get(248));
        _C3A.put("start",l.get(250));
        C3A = Collections.unmodifiableMap(_C3A);
        Map<String,Grm> _C3B = new HashMap<String,Grm>();
        _C3B.put("anything",l.get(0));
        l.add(new Grm.Terminal("mirror")); // 273
        l.add(new Grm.Concat(Tools.l(l.get(273),l.get(6)))); // 274
        l.add(new Grm.Alt(Tools.l(l.get(6),l.get(274)))); // 275
        _C3B.put("anything_0",l.get(275));
        l.add(new Grm.Nonterminal("anything_1_suffix",null,-1)); // 276
        l.add(new Grm.Mult(l.get(276),Grm.Mult.Type.STAR)); // 277
        l.add(new Grm.Concat(Tools.l(l.get(2),l.get(3),l.get(5),l.get(20),l.get(277)))); // 278
        l.add(new Grm.Nonterminal("anything_2","anything",1)); // 279
        l.add(new Grm.Concat(Tools.l(l.get(10),l.get(12),l.get(279),l.get(277)))); // 280
        l.add(new Grm.Alt(Tools.l(l.get(278),l.get(280)))); // 281
        l.add(new Grm.Concat(Tools.l(l.get(1),l.get(281)))); // 282
        l.add(new Grm.Concat(Tools.l(l.get(20),l.get(277)))); // 283
        l.add(new Grm.Alt(Tools.l(l.get(282),l.get(283)))); // 284
        _C3B.put("anything_1",l.get(284));
        _C3B.put("anything_1_suffix",l.get(260));
        l.add(new Grm.Concat(Tools.l(l.get(111),l.get(76),l.get(21)))); // 285
        l.add(new Grm.Alt(Tools.l(l.get(55),l.get(38),l.get(40),l.get(44),l.get(46),l.get(285),l.get(254),l.get(53),l.get(262)))); // 286
        _C3B.put("anything_2",l.get(286));
        l.add(new Grm.Nonterminal("anything_4",null,0)); // 287
        l.add(new Grm.Alt(Tools.l(l.get(287),l.get(57),l.get(60)))); // 288
        _C3B.put("anything_3",l.get(288));
        l.add(new Grm.Terminal("boomerang")); // 289
        l.add(new Grm.Terminal("scramble")); // 290
        l.add(new Grm.Terminal("bingo")); // 291
        l.add(new Grm.Terminal("nobody")); // 292
        l.add(new Grm.Terminal("everybody")); // 293
        l.add(new Grm.Alt(Tools.l(l.get(292),l.get(293)))); // 294
        l.add(new Grm.Concat(Tools.l(l.get(111),l.get(294)))); // 295
        l.add(new Grm.Terminal("golly")); // 296
        l.add(new Grm.Concat(Tools.l(l.get(171),l.get(296)))); // 297
        l.add(new Grm.Terminal("split")); // 298
        l.add(new Grm.Terminal("key")); // 299
        l.add(new Grm.Concat(Tools.l(l.get(298),l.get(34),l.get(76),l.get(299)))); // 300
        l.add(new Grm.Terminal("with")); // 301
        l.add(new Grm.Terminal("confidence")); // 302
        l.add(new Grm.Concat(Tools.l(l.get(301),l.get(302)))); // 303
        l.add(new Grm.Concat(Tools.l(l.get(76),l.get(299)))); // 304
        l.add(new Grm.Alt(Tools.l(l.get(123),l.get(304)))); // 305
        l.add(new Grm.Concat(Tools.l(l.get(34),l.get(305)))); // 306
        l.add(new Grm.Terminal("revolve")); // 307
        l.add(new Grm.Concat(Tools.l(l.get(307),l.get(110),l.get(4),l.get(111)))); // 308
        l.add(new Grm.Terminal("rip")); // 309
        l.add(new Grm.Concat(Tools.l(l.get(309),l.get(252)))); // 310
        l.add(new Grm.Terminal("explode")); // 311
        l.add(new Grm.Terminal("diamond")); // 312
        l.add(new Grm.Concat(Tools.l(l.get(311),l.get(76),l.get(312)))); // 313
        l.add(new Grm.Alt(Tools.l(l.get(167),l.get(79)))); // 314
        l.add(new Grm.Concat(Tools.l(l.get(76),l.get(314)))); // 315
        l.add(new Grm.Alt(Tools.l(l.get(315)))); // 316
        l.add(new Grm.Concat(Tools.l(l.get(58),l.get(316)))); // 317
        l.add(new Grm.Alt(Tools.l(l.get(62),l.get(179),l.get(63),l.get(289),l.get(64),l.get(65),l.get(66),l.get(290),l.get(291),l.get(67),l.get(73),l.get(56),l.get(59),l.get(74),l.get(295),l.get(297),l.get(78),l.get(81),l.get(83),l.get(86),l.get(271),l.get(88),l.get(300),l.get(303),l.get(90),l.get(93),l.get(306),l.get(96),l.get(97),l.get(99),l.get(101),l.get(106),l.get(308),l.get(304),l.get(108),l.get(114),l.get(118),l.get(121),l.get(257),l.get(125),l.get(127),l.get(310),l.get(129),l.get(132),l.get(313),l.get(317),l.get(133),l.get(36)))); // 318
        _C3B.put("anything_4",l.get(318));
        _C3B.put("leftable_anything",l.get(267));
        _C3B.put("reversable_anything",l.get(184));
        _C3B.put("parenthesized_anything",l.get(188));
        _C3B.put("people",l.get(191));
        _C3B.put("genders",l.get(194));
        _C3B.put("boys",l.get(197));
        _C3B.put("girls",l.get(200));
        _C3B.put("all",l.get(207));
        _C3B.put("wave_select",l.get(210));
        _C3B.put("anyone",l.get(213));
        _C3B.put("number",l.get(218));
        _C3B.put("digit",l.get(220));
        _C3B.put("digit_greater_than_two",l.get(227));
        _C3B.put("fraction",l.get(238));
        _C3B.put("cardinal",l.get(248));
        _C3B.put("start",l.get(250));
        C3B = Collections.unmodifiableMap(_C3B);
        Map<String,Grm> _C4 = new HashMap<String,Grm>();
        _C4.put("anything",l.get(0));
        _C4.put("anything_0",l.get(275));
        _C4.put("anything_1",l.get(284));
        _C4.put("anything_1_suffix",l.get(260));
        _C4.put("anything_2",l.get(286));
        _C4.put("anything_3",l.get(288));
        _C4.put("anything_4",l.get(318));
        _C4.put("leftable_anything",l.get(267));
        _C4.put("reversable_anything",l.get(184));
        _C4.put("parenthesized_anything",l.get(188));
        _C4.put("people",l.get(191));
        _C4.put("genders",l.get(194));
        _C4.put("boys",l.get(197));
        _C4.put("girls",l.get(200));
        _C4.put("all",l.get(207));
        _C4.put("wave_select",l.get(210));
        _C4.put("anyone",l.get(213));
        _C4.put("number",l.get(218));
        _C4.put("digit",l.get(220));
        _C4.put("digit_greater_than_two",l.get(227));
        _C4.put("fraction",l.get(238));
        _C4.put("cardinal",l.get(248));
        _C4.put("start",l.get(250));
        C4 = Collections.unmodifiableMap(_C4);
    }

}
