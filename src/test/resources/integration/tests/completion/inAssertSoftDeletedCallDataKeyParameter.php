<?php

use \Illuminate\Foundation\Testing\TestCase;

class Test extends TestCase {
    public function testIsItOk(): void
    {
        $this->assertSoftDeleted('users', ['<caret>' => '']);
    }
}
