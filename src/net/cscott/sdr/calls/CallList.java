package net.cscott.sdr.calls;

/** A list of calls. */
public class CallList {

    public static final Call TRADE =



	new OptList(COUPLE, new ParList(BEAU, new SeqList
					(new Primitive(1,3, QTR_RIGHT),
					 new SeqList
					 (new Primitive(1,-3, QTR_RIGHT),
					  null)),
					new ParList
					(BELLE, new SeqList
					 (new Primitive(-1,1,QTR_LEFT),
					  new SeqList
					  (new Primitive(-1,-1,QTR_LEFT),
					   null)),
					 null)),
		    new OptList
		    (MINIWAVE, new SeqList
		     (new Primitive(1,1,QTR_RIGHT),
		      new SeqList
		      (new Primitive(1,1,QTR_RIGHT),
		       null)),
		     null));
}
