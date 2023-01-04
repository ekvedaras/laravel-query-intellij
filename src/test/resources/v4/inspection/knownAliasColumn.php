<?php (new Illuminate\Database\Query\Builder())->from('testProject1.users AS alias1')->get('alias1.email');
(new Illuminate\Database\Query\Builder())->from('testProject1.users as alias1')->get('alias1.email');
