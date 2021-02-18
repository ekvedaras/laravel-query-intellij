<?php

class Demo {
    public function where($column, $operator, $value): self {
    }
}

(new Demo())->from('testProject1.users')->where('<caret>');