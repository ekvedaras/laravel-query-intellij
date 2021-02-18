<?php

class Demo {
    public function from($table): self {
    }
}

(new Demo())->from('testProject1.<caret>');