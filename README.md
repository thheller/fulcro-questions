Prep
```
npm install
npx shadow-cljs clj-repl
```

### Question #1: Always rendering from root?

What needs to be done to only render what actually changed? Is this even supported?

First compile and then open http://localhost:8700
```
(shadow/compile :parent-render)
```

