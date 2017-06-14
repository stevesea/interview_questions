# interview_questions

A repo for various interview questions encountered during never-ending job hunt.

At the moment, I'll probably code most in Kotlin since that's my new favorite.
Maybe use them as examples in future (or use to explore other programming languages).

## Questions

1. RPN calculator 
1. find 'connected' bodies in 2D array
1. threads that take turns
    * example:
        * t1: 1
        * t2: 2
        * t3: 3
        * t1: 4
        * t2: 5
        * t3: 6
        * t1: 7
        * t2: 8
        * t3: 9
1. validate & perform caculation for adding machine tape
    * input: ["12", "+", "17"] output: 29
    * only integers (including negative)
    * only + and - operators
    * validation can't have adjacent operators/numbers
    * _can_ have subtraction operator followed by negative number
    
1. sum nested integer lists
    * [1,2,3,[4,5,6,[7,8],9,10]]
1. powerset
    * [1,2,3] => [],[1],[2],[3],[1,2],[2,3],[1,3],[1,2,3]
1. RLE compress/decompress strings
    * don't use if result is longer than original string
1. Given integers L&R count X where L <= X <= R 
    * X = a<sup>p</sup> + b<sup>q</sup> 
    * a,b >= 0
    * p,q > 1
1. reverse a string
1. fibonacci
1. implement itoa

## todo 
1. in java, create a REST GET API that validates word is palindrome. 
    * on valid, return 200 & json obj
    * on invalid, return 400 BAD REQ w/ message body that includes data showing which chars are non-matching
    * use whatever libraries/frameworks familiar with
1. describe effect of bad hashcode impl
1. method for validating matching block delims 
    ```
    [[[(({{}}))()([])]]]
    ```
1. given Node interface for tree, find nearest common ancestor
    ```java
    interface Node {
            Node parent;
            List<Node> children;
    }
    ```
    
