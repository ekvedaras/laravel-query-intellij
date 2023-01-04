<?php

DB::table('users')
    ->where('email', '!=', '"test"')
    ->update(['email' => '"test"']);
