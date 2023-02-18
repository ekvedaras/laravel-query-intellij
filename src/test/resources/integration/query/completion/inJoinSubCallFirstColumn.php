<?php

$query = DB::query()->from('customers')->select('billable_id');

DB::query()->from('users')->joinSub($query, 'c2', '<caret>');
