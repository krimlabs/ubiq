repl:
	clj -A:dev -Sdeps '{:deps {cider/cider-nrepl {:mvn/version "0.21.1"}}}' -m nrepl.cmdline --middleware "[cider.nrepl/cider-middleware]"
